package io.paymentgateway.core.extended.tenant.service.kyc;

import static io.paymentgateway.core.extended.tenant.domain.KycSignal.NAME_MISMATCH;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.NO_DIRECTORS;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.PEP_HIT;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.REGISTRY_DISSOLVED;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.REGISTRY_NOT_FOUND;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.REGISTRY_UNAVAILABLE;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.SANCTIONS_HIT;
import static io.paymentgateway.core.extended.tenant.domain.KycSignal.SCREENING_UNAVAILABLE;

import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Runs the sanctions/PEP screening and the registry cross-reference and reports what they found as {@link KycSignal}s.
 * A provider that fails to answer is reported as an "unavailable" signal rather than as a clean result, so an outage can
 * only ever push an application towards manual review, never towards approval.
 */
@Component
public class KycSignalCollector {

    private static final Logger LOG = LoggerFactory.getLogger(KycSignalCollector.class);

    private final ComplianceScreeningProvider screening;
    private final BusinessRegistryClient registry;

    public KycSignalCollector(ComplianceScreeningProvider screening, BusinessRegistryClient registry) {
        this.screening = screening;
        this.registry = registry;
    }

    public Set<KycSignal> collect(OnboardingSnapshot snapshot) {
        Set<KycSignal> signals = EnumSet.noneOf(KycSignal.class);
        screenParties(snapshot, signals);
        crossReferenceRegistry(snapshot, signals);
        return signals;
    }

    private void screenParties(OnboardingSnapshot snapshot, Set<KycSignal> signals) {
        if (snapshot.directors().isEmpty()) {
            signals.add(NO_DIRECTORS);
        }
        try {
            record(screening.screenBusiness(snapshot.legalBusinessName(), snapshot.jurisdiction()), signals);
            for (PartyToScreen director : snapshot.directors()) {
                record(screening.screenPerson(director), signals);
            }
        } catch (RuntimeException e) {
            LOG.warn("Sanctions/PEP screening failed for tenant {}: {}", snapshot.tenantId(), e.toString());
            signals.add(SCREENING_UNAVAILABLE);
        }
    }

    private void crossReferenceRegistry(OnboardingSnapshot snapshot, Set<KycSignal> signals) {
        try {
            RegistryRecord found = registry.lookup(snapshot.businessRegistrationNumber(), snapshot.jurisdiction());
            switch (found.status()) {
                case DISSOLVED -> signals.add(REGISTRY_DISSOLVED);
                case NOT_FOUND -> signals.add(REGISTRY_NOT_FOUND);
                case ACTIVE -> {
                    if (found.registeredName() != null && !sameName(found.registeredName(), snapshot.legalBusinessName())) {
                        signals.add(NAME_MISMATCH);
                    }
                }
            }
        } catch (RuntimeException e) {
            LOG.warn("Registry lookup failed for tenant {}: {}", snapshot.tenantId(), e.toString());
            signals.add(REGISTRY_UNAVAILABLE);
        }
    }

    private static void record(SanctionsScreeningResult result, Set<KycSignal> signals) {
        if (result.sanctionsHit()) {
            signals.add(SANCTIONS_HIT);
        }
        if (result.pepHit()) {
            signals.add(PEP_HIT);
        }
    }

    /** Case-, spacing- and punctuation-insensitive comparison of legal names. */
    static boolean sameName(String a, String b) {
        return normalise(a).equals(normalise(b));
    }

    private static String normalise(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
    }
}
