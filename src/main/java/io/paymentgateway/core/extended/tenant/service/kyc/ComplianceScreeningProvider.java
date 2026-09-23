package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.domain.enumeration.CountryCode;

/**
 * Sanctions and PEP screening call-out (spec Chunk 2 §2.2 step 1: OFAC, EU, UN lists via e.g. ComplyAdvantage / Smile
 * Identity). Implementations return findings, not scores: turning findings into a risk score is {@link KycScoringPolicy}'s job,
 * so scoring stays vendor-independent.
 */
public interface ComplianceScreeningProvider {
    SanctionsScreeningResult screenPerson(PartyToScreen person) throws ScreeningProviderException;

    SanctionsScreeningResult screenBusiness(String legalBusinessName, CountryCode jurisdiction) throws ScreeningProviderException;
}
