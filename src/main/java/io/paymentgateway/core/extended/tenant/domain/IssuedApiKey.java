package io.paymentgateway.core.extended.tenant.domain;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import java.time.Instant;

/**
 * A newly issued key. {@code secret} is the only time the raw secret exists outside the merchant's hands: it is shown once
 * and only its hash is stored.
 */
public record IssuedApiKey(Long id, String keyPrefix, ApiEnvironment environment, String secret, Instant issuedAt) {
    /** Keeps the secret out of logs and exception messages. */
    @Override
    public String toString() {
        return "IssuedApiKey[id=" + id + ", keyPrefix=" + keyPrefix + ", environment=" + environment + ", secret=***]";
    }
}
