package io.paymentgateway.core.extended.tenant.domain;

/** A recognised, usable key together with whether its tenant may currently process requests in the key's environment. */
public record ApiKeyAuthResult(ApiKeyPrincipal principal, TenantAccessDecision decision) {}
