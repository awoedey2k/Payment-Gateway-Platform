package io.paymentgateway.core.extended.tenant.config;

import io.paymentgateway.core.extended.tenant.properties.TenantProperties;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyGenerator;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyHasher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/** Beans and start-up safety checks for the Tenant module. */
@Configuration
@EnableConfigurationProperties(TenantProperties.class)
public class TenantConfiguration {

    static final int MIN_PRODUCTION_PEPPER_LENGTH = 32;

    @Bean
    public ApiKeyHasher apiKeyHasher(TenantProperties properties, Environment environment) {
        requireSafeForEnvironment(properties, environment.acceptsProfiles(Profiles.of("prod")));
        return new ApiKeyHasher(properties.apiKeys().pepper());
    }

    @Bean
    public ApiKeyGenerator apiKeyGenerator() {
        return new ApiKeyGenerator();
    }

    /**
     * Fails fast, before serving traffic, if production is configured with the development pepper or the stub KYC provider.
     * Both would be silent security failures (guessable key hashes; every applicant auto-approved).
     */
    static void requireSafeForEnvironment(TenantProperties properties, boolean production) {
        if (!production) {
            return;
        }
        String pepper = properties.apiKeys().pepper();
        if (TenantProperties.DEV_PEPPER.equals(pepper) || pepper.length() < MIN_PRODUCTION_PEPPER_LENGTH) {
            throw new IllegalStateException(
                "payment-gateway.tenant.api-keys.pepper must be set to a secret of at least " +
                    MIN_PRODUCTION_PEPPER_LENGTH +
                    " characters under the prod profile"
            );
        }
        if ("stub".equals(properties.kyc().provider())) {
            throw new IllegalStateException(
                "payment-gateway.tenant.kyc.provider=stub is not allowed under the prod profile; configure a real KYC provider"
            );
        }
    }
}
