package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import java.util.List;

/** Everything the screening call-outs need, copied out of the database so no transaction is open while providers are called. */
public record OnboardingSnapshot(
    Long tenantId,
    String legalBusinessName,
    String businessRegistrationNumber,
    CountryCode jurisdiction,
    List<PartyToScreen> directors
) {}
