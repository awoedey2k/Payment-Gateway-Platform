package io.paymentgateway.core.extended.tenant.web;

import io.paymentgateway.core.extended.tenant.service.checkout.CheckoutDomainService;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.CheckoutConfigResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unauthenticated checkout-page bootstrap: resolves the tenant from the request's {@code Host} (a verified custom CNAME
 * domain) and negotiates the locale from {@code Accept-Language}. Returns 404 for unknown or unverified domains.
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicCheckoutResource {

    private final CheckoutDomainService checkoutDomains;

    public PublicCheckoutResource(CheckoutDomainService checkoutDomains) {
        this.checkoutDomains = checkoutDomains;
    }

    @GetMapping("/checkout-config")
    public ResponseEntity<CheckoutConfigResponse> checkoutConfig(
        @RequestHeader(HttpHeaders.HOST) String host,
        @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage
    ) {
        return checkoutDomains
            .resolve(host, acceptLanguage)
            .map(c -> new CheckoutConfigResponse(c.customDomain(), c.locale(), c.supportedLocales()))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
