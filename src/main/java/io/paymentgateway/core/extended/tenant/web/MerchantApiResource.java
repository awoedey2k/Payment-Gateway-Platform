package io.paymentgateway.core.extended.tenant.web;

import io.paymentgateway.core.extended.tenant.domain.ApiKeyPrincipal;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.WhoAmIResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Merchant-facing endpoints authenticated by API key (see {@code TenantSecurityConfiguration}). */
@RestController
@RequestMapping("/api/v1")
public class MerchantApiResource {

    /** Lets a merchant confirm which tenant and environment a key belongs to; also the simplest end-to-end auth check. */
    @GetMapping("/whoami")
    public WhoAmIResponse whoAmI(@AuthenticationPrincipal ApiKeyPrincipal principal) {
        return new WhoAmIResponse(principal.tenantId(), principal.apiKeyId(), principal.environment());
    }
}
