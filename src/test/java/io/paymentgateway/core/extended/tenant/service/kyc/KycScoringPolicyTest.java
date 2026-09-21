package io.paymentgateway.core.extended.tenant.service.kyc;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.extended.tenant.domain.KycAssessment;
import io.paymentgateway.core.extended.tenant.domain.KycDecision;
import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import io.paymentgateway.core.extended.tenant.support.TestTenantProperties;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** The three score bands of spec Figure 2.2: 0-20 approve, 21-60 manual review, 61-100 reject. */
class KycScoringPolicyTest {

    private final KycScoringPolicy policy = new KycScoringPolicy(TestTenantProperties.defaults());

    @ParameterizedTest(name = "score {0} -> {1}")
    @CsvSource({
        "0,APPROVE",
        "1,APPROVE",
        "20,APPROVE",
        "21,MANUAL_REVIEW",
        "40,MANUAL_REVIEW",
        "60,MANUAL_REVIEW",
        "61,REJECT",
        "99,REJECT",
        "100,REJECT",
    })
    void bandBoundaries(int score, KycDecision expected) {
        assertThat(KycScoringPolicy.decisionFor(score)).isEqualTo(expected);
    }

    @Test
    void noSignalsScoresZeroAndApproves() {
        KycAssessment result = policy.assess(EnumSet.noneOf(KycSignal.class));
        assertThat(result.score()).isZero();
        assertThat(result.decision()).isEqualTo(KycDecision.APPROVE);
    }

    @Test
    void aSanctionsHitAloneIsRejected() {
        KycAssessment result = policy.assess(Set.of(KycSignal.SANCTIONS_HIT));
        assertThat(result.score()).isBetween(61, 100);
        assertThat(result.decision()).isEqualTo(KycDecision.REJECT);
    }

    @Test
    void aPepHitAloneGoesToManualReview() {
        KycAssessment result = policy.assess(Set.of(KycSignal.PEP_HIT));
        assertThat(result.score()).isBetween(21, 60);
        assertThat(result.decision()).isEqualTo(KycDecision.MANUAL_REVIEW);
    }

    @Test
    void aDissolvedCompanyIsRejected() {
        assertThat(policy.assess(Set.of(KycSignal.REGISTRY_DISSOLVED)).decision()).isEqualTo(KycDecision.REJECT);
    }

    @Test
    void everySingleSignalStopsAutoApproval() {
        for (KycSignal signal : KycSignal.values()) {
            KycAssessment result = policy.assess(Set.of(signal));
            assertThat(result.score()).as("score for %s", signal).isGreaterThan(KycScoringPolicy.CLEAN_MAX);
            assertThat(result.decision()).as("decision for %s", signal).isNotEqualTo(KycDecision.APPROVE);
        }
    }

    @Test
    void signalsAccumulateAcrossTheBandBoundary() {
        // 40 (PEP) + 30 (registry unavailable) = 70 -> crosses from manual review into reject
        KycAssessment result = policy.assess(Set.of(KycSignal.PEP_HIT, KycSignal.REGISTRY_UNAVAILABLE));
        assertThat(result.score()).isEqualTo(70);
        assertThat(result.decision()).isEqualTo(KycDecision.REJECT);
    }

    @Test
    void scoreIsCappedAtOneHundred() {
        KycAssessment result = policy.assess(EnumSet.allOf(KycSignal.class));
        assertThat(result.score()).isEqualTo(100);
        assertThat(result.decision()).isEqualTo(KycDecision.REJECT);
    }

    @Test
    void aSanctionsHitCannotBeDilutedByMisTunedWeights() {
        KycScoringPolicy lowWeights = new KycScoringPolicy(
            TestTenantProperties.with(Map.of("payment-gateway.tenant.kyc.weights.sanctions-hit", "5"))
        );
        KycAssessment result = lowWeights.assess(Set.of(KycSignal.SANCTIONS_HIT));
        assertThat(result.score()).isGreaterThanOrEqualTo(61);
        assertThat(result.decision()).isEqualTo(KycDecision.REJECT);
    }

    @Test
    void weightsAreConfigurable() {
        KycScoringPolicy custom = new KycScoringPolicy(
            TestTenantProperties.with(Map.of("payment-gateway.tenant.kyc.weights.pep-hit", "15"))
        );
        assertThat(custom.assess(Set.of(KycSignal.PEP_HIT)).decision()).isEqualTo(KycDecision.APPROVE);
    }

    @Test
    void assessmentKeepsTheSignalsThatProducedIt() {
        assertThat(policy.assess(Set.of(KycSignal.NAME_MISMATCH)).signals()).containsExactly(KycSignal.NAME_MISMATCH);
    }

    private static KycScoringPolicy policyWith(String nameMismatch, String registryUnavailable) {
        return new KycScoringPolicy(
            TestTenantProperties.with(
                Map.of(
                    "payment-gateway.tenant.kyc.weights.name-mismatch",
                    nameMismatch,
                    "payment-gateway.tenant.kyc.weights.registry-unavailable",
                    registryUnavailable
                )
            )
        );
    }

    /** The band boundaries exercised through assess() (signals -> score -> decision), not just decisionFor(int). */
    @ParameterizedTest(name = "{0}+{1} -> score {2} -> {3}")
    @CsvSource({ "20,0,20,APPROVE", "21,0,21,MANUAL_REVIEW", "30,30,60,MANUAL_REVIEW", "31,30,61,REJECT" })
    void exactBandBoundariesThroughAssess(String nameMismatch, String registryUnavailable, int expectedScore, KycDecision expected) {
        // registry-unavailable of 0 is only used to omit that signal below; the policy itself does not validate weights.
        KycScoringPolicy policy = policyWith(nameMismatch, registryUnavailable);
        Set<KycSignal> signals = "0".equals(registryUnavailable)
            ? Set.of(KycSignal.NAME_MISMATCH)
            : Set.of(KycSignal.NAME_MISMATCH, KycSignal.REGISTRY_UNAVAILABLE);
        KycAssessment result = policy.assess(signals);
        assertThat(result.score()).isEqualTo(expectedScore);
        assertThat(result.decision()).isEqualTo(expected);
    }
}
