package io.paymentgateway.core.extended.tenant.web.dto;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response shapes of the Tenant module's REST endpoints. */
public final class TenantDtos {

    private TenantDtos() {}

    public record StatusChangeRequest(@NotNull TenantStatus targetStatus, @NotBlank String reason) {}

    public record DecisionRequest(@NotBlank String reason) {}

    public record IssueKeyRequest(@NotNull ApiEnvironment environment) {}

    /** {@code gracePeriodHours} is optional; the configured default (48h) applies when omitted. */
    public record RotateKeyRequest(@NotNull ApiEnvironment environment, Integer gracePeriodHours) {}

    public record TenantStatusResponse(Long id, TenantStatus status, KycStatus kycStatus, Integer riskScore) {}

    public record WhoAmIResponse(Long tenantId, Long apiKeyId, ApiEnvironment environment) {}

    /** Public checkout configuration. Deliberately omits internal identifiers. */
    public record CheckoutConfigResponse(String customDomain, String locale, List<String> supportedLocales) {}
}
