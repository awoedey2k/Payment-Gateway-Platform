package io.paymentgateway.core.extended.tenant.domain;

import java.util.List;

/** What the checkout page needs once a custom domain has been mapped to a tenant. */
public record CheckoutConfig(Long tenantId, String customDomain, String locale, List<String> supportedLocales) {}
