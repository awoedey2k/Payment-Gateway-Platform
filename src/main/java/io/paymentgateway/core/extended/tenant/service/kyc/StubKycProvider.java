package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import java.util.Locale;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Deterministic, test-data-driven stand-in for the real screening provider and registry (none is provisioned yet).
 * <b>Not for production</b>: it approves anything that does not match a trigger below, so the application refuses to start
 * with it under the {@code prod} profile. Active while {@code payment-gateway.tenant.kyc.provider} is {@code stub} (default).
 *
 * <ul>
 *   <li>Name containing {@code SANCTIONED} -> sanctions hit; containing {@code PEP} -> PEP hit (persons and business).
 *   <li>Registration number starting {@code DISSOLVED-} -> dissolved; {@code UNKNOWN-} -> not found; {@code UNAVAILABLE-} ->
 *       provider failure; {@code MISMATCH-} -> registered under a different name.
 *   <li>Anything else -> active, registered under the submitted name.
 * </ul>
 */
@Component
@ConditionalOnProperty(prefix = "payment-gateway.tenant.kyc", name = "provider", havingValue = "stub", matchIfMissing = true)
public class StubKycProvider implements ComplianceScreeningProvider, BusinessRegistryClient {

    @Override
    public SanctionsScreeningResult screenPerson(PartyToScreen person) {
        return byName(person.fullName());
    }

    @Override
    public SanctionsScreeningResult screenBusiness(String legalBusinessName, CountryCode jurisdiction) {
        return byName(legalBusinessName);
    }

    @Override
    public RegistryRecord lookup(String registrationNumber, CountryCode jurisdiction) {
        String number = registrationNumber.toUpperCase(Locale.ROOT);
        if (number.startsWith("UNAVAILABLE-")) {
            throw new ScreeningProviderException("stub registry unavailable");
        }
        if (number.startsWith("DISSOLVED-")) {
            return new RegistryRecord(RegistryStatus.DISSOLVED, "Dissolved Registered Name");
        }
        if (number.startsWith("UNKNOWN-")) {
            return new RegistryRecord(RegistryStatus.NOT_FOUND, null);
        }
        if (number.startsWith("MISMATCH-")) {
            return new RegistryRecord(RegistryStatus.ACTIVE, "A Completely Different Name Ltd");
        }
        return new RegistryRecord(RegistryStatus.ACTIVE, null);
    }

    private static SanctionsScreeningResult byName(String name) {
        String upper = name.toUpperCase(Locale.ROOT);
        return new SanctionsScreeningResult(upper.contains("SANCTIONED"), upper.contains("PEP"));
    }
}
