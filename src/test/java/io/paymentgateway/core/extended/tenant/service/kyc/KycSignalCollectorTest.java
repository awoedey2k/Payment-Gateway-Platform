package io.paymentgateway.core.extended.tenant.service.kyc;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class KycSignalCollectorTest {

    private static final PartyToScreen DIRECTOR = new PartyToScreen("Sarah Doe", LocalDate.of(1984, 6, 15), CountryCode.NG);

    private static OnboardingSnapshot snapshot(String regNo, List<PartyToScreen> directors) {
        return new OnboardingSnapshot(1L, "Apex Technologies Global Limited", regNo, CountryCode.NG, directors);
    }

    private static ComplianceScreeningProvider screening(Function<String, SanctionsScreeningResult> byName) {
        return new ComplianceScreeningProvider() {
            public SanctionsScreeningResult screenPerson(PartyToScreen p) {
                return byName.apply(p.fullName());
            }

            public SanctionsScreeningResult screenBusiness(String name, CountryCode c) {
                return byName.apply(name);
            }
        };
    }

    private static final ComplianceScreeningProvider CLEAR = screening(n -> SanctionsScreeningResult.CLEAR);
    private static final BusinessRegistryClient ACTIVE = (r, j) ->
        new RegistryRecord(RegistryStatus.ACTIVE, "Apex Technologies Global Limited");

    @Test
    void aCleanApplicationProducesNoSignals() {
        assertThat(new KycSignalCollector(CLEAR, ACTIVE).collect(snapshot("RC-1", List.of(DIRECTOR)))).isEmpty();
    }

    @Test
    void sanctionsAndPepHitsOnAnyDirectorAreReported() {
        var screening = screening(n -> new SanctionsScreeningResult(n.contains("Bad"), n.contains("Politician")));
        var collector = new KycSignalCollector(screening, ACTIVE);
        var directors = List.of(
            DIRECTOR,
            new PartyToScreen("Bad Actor", LocalDate.of(1970, 1, 1), CountryCode.GB),
            new PartyToScreen("Politician X", LocalDate.of(1970, 1, 1), CountryCode.GB)
        );
        assertThat(collector.collect(snapshot("RC-1", directors))).containsExactlyInAnyOrder(KycSignal.SANCTIONS_HIT, KycSignal.PEP_HIT);
    }

    @Test
    void theBusinessItselfIsScreenedToo() {
        var screening = screening(n -> new SanctionsScreeningResult(n.contains("Apex"), false));
        assertThat(new KycSignalCollector(screening, ACTIVE).collect(snapshot("RC-1", List.of(DIRECTOR)))).containsExactly(
            KycSignal.SANCTIONS_HIT
        );
    }

    @Test
    void noDirectorsIsASignalBecauseNothingCanBeScreened() {
        assertThat(new KycSignalCollector(CLEAR, ACTIVE).collect(snapshot("RC-1", List.of()))).containsExactly(KycSignal.NO_DIRECTORS);
    }

    @Test
    void registryStatusesMapToSignals() {
        assertThat(collectWith(new RegistryRecord(RegistryStatus.DISSOLVED, "x"))).containsExactly(KycSignal.REGISTRY_DISSOLVED);
        assertThat(collectWith(new RegistryRecord(RegistryStatus.NOT_FOUND, null))).containsExactly(KycSignal.REGISTRY_NOT_FOUND);
    }

    private java.util.Set<KycSignal> collectWith(RegistryRecord record) {
        return new KycSignalCollector(CLEAR, (r, j) -> record).collect(snapshot("RC-1", List.of(DIRECTOR)));
    }

    @Test
    void legalNameComparisonIgnoresCaseSpacingAndPunctuation() {
        assertThat(collectWith(new RegistryRecord(RegistryStatus.ACTIVE, "APEX TECHNOLOGIES, GLOBAL LIMITED."))).isEmpty();
        assertThat(collectWith(new RegistryRecord(RegistryStatus.ACTIVE, "Some Other Company Ltd"))).containsExactly(
            KycSignal.NAME_MISMATCH
        );
    }

    @Test
    void aRegistryOutageIsARiskSignalNotAClearResult() {
        BusinessRegistryClient down = (r, j) -> {
            throw new ScreeningProviderException("timeout");
        };
        assertThat(new KycSignalCollector(CLEAR, down).collect(snapshot("RC-1", List.of(DIRECTOR)))).containsExactly(
            KycSignal.REGISTRY_UNAVAILABLE
        );
    }

    @Test
    void aScreeningOutageOrUnexpectedFailureIsARiskSignalNotAClearResult() {
        ComplianceScreeningProvider down = screening(n -> {
            throw new IllegalStateException("provider exploded");
        });
        assertThat(new KycSignalCollector(down, ACTIVE).collect(snapshot("RC-1", List.of(DIRECTOR)))).containsExactly(
            KycSignal.SCREENING_UNAVAILABLE
        );
    }

    @Test
    void screeningAndRegistryFailuresAreIndependent() {
        ComplianceScreeningProvider down = screening(n -> {
            throw new ScreeningProviderException("down");
        });
        BusinessRegistryClient notFound = (r, j) -> new RegistryRecord(RegistryStatus.NOT_FOUND, null);
        assertThat(new KycSignalCollector(down, notFound).collect(snapshot("RC-1", List.of(DIRECTOR)))).containsExactlyInAnyOrder(
            KycSignal.SCREENING_UNAVAILABLE,
            KycSignal.REGISTRY_NOT_FOUND
        );
    }
}
