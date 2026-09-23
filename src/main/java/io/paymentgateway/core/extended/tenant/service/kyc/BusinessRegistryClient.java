package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.domain.enumeration.CountryCode;

/** Registry cross-reference (spec Chunk 2 §2.2 step 2): CAC in Nigeria, Registrar of Companies in Kenya, Companies House in the UK. */
public interface BusinessRegistryClient {
    RegistryRecord lookup(String registrationNumber, CountryCode jurisdiction) throws ScreeningProviderException;
}
