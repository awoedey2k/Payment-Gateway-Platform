package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.extended.tenant.domain.KycAssessment;
import io.paymentgateway.core.extended.tenant.domain.KycDecision;
import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import io.paymentgateway.core.extended.tenant.properties.TenantProperties;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Turns KYC signals into the 0-100 risk score and the outcome band (spec Chunk 2 §2.2 / Figure 2.2):
 *
 * <pre>
 *  0-20   clean          -> APPROVE
 * 21-60   medium risk    -> MANUAL_REVIEW (Tier-2, within 24h)
 * 61-100  critical       -> REJECT
 * </pre>
 *
 * The score is the sum of the configured weight of each distinct signal, capped at 100. A sanctions hit is floored at 61 so
 * mis-tuned weights can never let one through. No signals means score 0; because every default weight exceeds 20, any
 * single signal at least routes the application to manual review, so an incomplete check never auto-approves.
 */
@Component
public class KycScoringPolicy {

    public static final int MAX_SCORE = 100;
    public static final int CLEAN_MAX = 20;
    public static final int MEDIUM_MAX = 60;

    private final TenantProperties.Weights weights;

    public KycScoringPolicy(TenantProperties properties) {
        this.weights = properties.kyc().weights();
    }

    public KycAssessment assess(Set<KycSignal> signals) {
        int score = 0;
        for (KycSignal signal : signals) {
            score += weightOf(signal);
        }
        score = Math.min(MAX_SCORE, score);
        if (signals.contains(KycSignal.SANCTIONS_HIT)) {
            score = Math.max(score, MEDIUM_MAX + 1);
        }
        return new KycAssessment(score, signals, decisionFor(score));
    }

    public static KycDecision decisionFor(int score) {
        if (score <= CLEAN_MAX) {
            return KycDecision.APPROVE;
        }
        return score <= MEDIUM_MAX ? KycDecision.MANUAL_REVIEW : KycDecision.REJECT;
    }

    private int weightOf(KycSignal signal) {
        return switch (signal) {
            case SANCTIONS_HIT -> weights.sanctionsHit();
            case PEP_HIT -> weights.pepHit();
            case REGISTRY_DISSOLVED -> weights.registryDissolved();
            case REGISTRY_NOT_FOUND -> weights.registryNotFound();
            case REGISTRY_UNAVAILABLE -> weights.registryUnavailable();
            case NAME_MISMATCH -> weights.nameMismatch();
            case NO_DIRECTORS -> weights.noDirectors();
            case SCREENING_UNAVAILABLE -> weights.screeningUnavailable();
        };
    }
}
