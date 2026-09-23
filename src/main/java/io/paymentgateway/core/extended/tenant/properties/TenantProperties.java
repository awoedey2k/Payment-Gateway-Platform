package io.paymentgateway.core.extended.tenant.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration for the Tenant module ({@code payment-gateway.tenant.*}).
 *
 * <p>Lives outside the {@code config} package on purpose: the generated ArchUnit layer rules forbid other layers from
 * depending on {@code ..config..}.
 */
@ConfigurationProperties(prefix = "payment-gateway.tenant")
public record TenantProperties(@DefaultValue ApiKeys apiKeys, @DefaultValue Kyc kyc) {
    /** Placeholder pepper used only when none is configured. Refused under the {@code prod} profile. */
    public static final String DEV_PEPPER = "dev-only-pepper-not-for-production-use";

    public record ApiKeys(
        /* Server-side secret mixed into every API-key hash (HMAC key). Supply via env in production. */
        @DefaultValue(DEV_PEPPER) String pepper,
        /* Dual-active window used when a rotation request does not specify one (spec: 24-48h). */
        @DefaultValue("48h") Duration defaultGracePeriod,
        @DefaultValue("1h") Duration minGracePeriod,
        @DefaultValue("48h") Duration maxGracePeriod
    ) {}

    public record Kyc(
        /* "stub" is a deterministic, test-data-driven adapter; it is refused under the prod profile. */
        @DefaultValue("stub") String provider,
        @DefaultValue Weights weights
    ) {}

    /** Risk points added per signal; the total is capped at 100. Every weight should exceed 20 so a single signal never auto-approves. */
    public record Weights(
        @DefaultValue("70") int sanctionsHit,
        @DefaultValue("40") int pepHit,
        @DefaultValue("65") int registryDissolved,
        @DefaultValue("50") int registryNotFound,
        @DefaultValue("30") int registryUnavailable,
        @DefaultValue("30") int nameMismatch,
        @DefaultValue("25") int noDirectors,
        @DefaultValue("30") int screeningUnavailable
    ) {}
}
