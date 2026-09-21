package io.paymentgateway.core.extended.tenant.service.apikey;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically flags keys whose rotation grace period has lapsed as inactive (spec: "changing Key A.is_active to false").
 * Authentication does not depend on this job: {@link ApiKeyService#authenticate} already refuses a key past its grace
 * period. Interval: {@code payment-gateway.tenant.api-keys.expiry-sweep-interval} (ISO-8601 duration, default {@code PT1M}).
 */
@Component
public class ApiKeyExpirySweeper {

    private final ApiKeyService apiKeys;

    public ApiKeyExpirySweeper(ApiKeyService apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Scheduled(fixedDelayString = "${payment-gateway.tenant.api-keys.expiry-sweep-interval:PT1M}")
    public void sweep() {
        apiKeys.expireLapsedKeys();
    }
}
