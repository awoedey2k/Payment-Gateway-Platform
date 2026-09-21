package io.paymentgateway.core.extended.tenant.config;

import io.paymentgateway.core.extended.tenant.properties.TenantProperties;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyGenerator;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyHasher;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Beans and start-up safety checks for the Tenant module. The checks run before any bean that depends on this class is
 * created, so an unsafe configuration stops the application from starting instead of failing open.
 */
@Configuration
@EnableConfigurationProperties(TenantProperties.class)
public class TenantConfiguration implements InitializingBean {

    static final int MIN_PRODUCTION_PEPPER_LENGTH = 32;
    static final int CLEAN_SCORE_CEILING = 20;

    private final TenantProperties properties;
    private final Environment environment;

    public TenantConfiguration(TenantProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        requireValidWeights(properties.kyc().weights());
        // Fail closed for every *named* environment other than dev/test: "prod", "staging", ... cannot run on the public dev
        // pepper or the stub. (No profile at all counts as development: JHipster builds always set one, and the generated
        // Cucumber tests run without any.)
        requireSafeForEnvironment(properties, !isDevelopmentOrTest(environment.getActiveProfiles()));
    }

    @Bean
    public ApiKeyHasher apiKeyHasher() {
        return new ApiKeyHasher(properties.apiKeys().pepper());
    }

    @Bean
    public ApiKeyGenerator apiKeyGenerator() {
        return new ApiKeyGenerator();
    }

    static boolean isDevelopmentOrTest(String[] activeProfiles) {
        return activeProfiles.length == 0 || Arrays.stream(activeProfiles).anyMatch(p -> p.equals("dev") || p.startsWith("test"));
    }

    /**
     * Every weight must exceed the auto-approve ceiling (20) and be at most 100. A weight of 20 or less would let a finding
     * such as a PEP hit or a registry outage still auto-approve; a negative one would cancel out other findings.
     */
    static void requireValidWeights(TenantProperties.Weights w) {
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("sanctions-hit", w.sanctionsHit());
        weights.put("pep-hit", w.pepHit());
        weights.put("registry-dissolved", w.registryDissolved());
        weights.put("registry-not-found", w.registryNotFound());
        weights.put("registry-unavailable", w.registryUnavailable());
        weights.put("name-mismatch", w.nameMismatch());
        weights.put("no-directors", w.noDirectors());
        weights.put("screening-unavailable", w.screeningUnavailable());
        weights.forEach((name, value) -> {
            if (value <= CLEAN_SCORE_CEILING || value > 100) {
                throw new IllegalStateException(
                    "payment-gateway.tenant.kyc.weights." +
                        name +
                        "=" +
                        value +
                        " is invalid: must be between " +
                        (CLEAN_SCORE_CEILING + 1) +
                        " and 100 so a single finding can never auto-approve"
                );
            }
        });
    }

    /**
     * Refuses to start production with the development pepper (guessable key hashes) or the stub KYC provider (every
     * applicant auto-approved).
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
                    " characters unless the dev or a test profile is active"
            );
        }
        if ("stub".equals(properties.kyc().provider())) {
            throw new IllegalStateException(
                "payment-gateway.tenant.kyc.provider=stub is not allowed unless the dev or a test profile is active; configure a real KYC provider"
            );
        }
    }
}
