package io.paymentgateway.core.extended.tenant.domain;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;

/** The authenticated merchant behind a valid API key. Never carries the secret. */
public record ApiKeyPrincipal(Long apiKeyId, Long tenantId, ApiEnvironment environment) {}
