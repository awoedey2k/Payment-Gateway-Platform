package io.paymentgateway.core.extended.tenant.service.apikey;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.extended.tenant.domain.ApiKeyAuthResult;
import io.paymentgateway.core.extended.tenant.domain.ApiKeyPrincipal;
import io.paymentgateway.core.extended.tenant.domain.IssuedApiKey;
import io.paymentgateway.core.extended.tenant.domain.KeyRotationResult;
import io.paymentgateway.core.extended.tenant.domain.TenantAccessDecision;
import io.paymentgateway.core.extended.tenant.properties.TenantProperties;
import io.paymentgateway.core.extended.tenant.repository.ExtendedApiKeyRepository;
import io.paymentgateway.core.extended.tenant.repository.ExtendedCorporateTenantRepository;
import io.paymentgateway.core.extended.tenant.service.TenantAccessGuard;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API key issuance, dual-active-window rotation, revocation and authentication (spec Chunk 2 §4).
 *
 * <p>Concurrency: issue, rotate and revoke all take {@code SELECT ... FOR UPDATE} on the tenant row first, so concurrent
 * operations for one tenant are serialised and the "at most two usable keys per tenant and environment" invariant holds;
 * other tenants are never blocked. Authentication is read-only and takes no lock.
 *
 * <p>A key is <em>usable</em> when it is active, not revoked, and its grace period (if any) has not lapsed. Lapsing is
 * evaluated against the clock on every authentication, so a key stops working the instant its grace ends; the periodic
 * sweep only tidies the {@code isActive} flag.
 */
@Service
public class ApiKeyService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyService.class);

    private final ExtendedCorporateTenantRepository tenants;
    private final ExtendedApiKeyRepository keys;
    private final ApiKeyHasher hasher;
    private final ApiKeyGenerator generator;
    private final TenantProperties.ApiKeys settings;
    private final Clock clock;

    public ApiKeyService(
        ExtendedCorporateTenantRepository tenants,
        ExtendedApiKeyRepository keys,
        ApiKeyHasher hasher,
        ApiKeyGenerator generator,
        TenantProperties properties,
        Clock clock
    ) {
        this.tenants = tenants;
        this.keys = keys;
        this.hasher = hasher;
        this.generator = generator;
        this.settings = properties.apiKeys();
        this.clock = clock;
    }

    /** Issues the first key for a tenant/environment. Use {@link #rotate} when a usable key already exists. */
    @Transactional
    public IssuedApiKey issueKey(Long tenantId, ApiEnvironment environment) {
        CorporateTenant tenant = lockTenant(tenantId);
        requireEligible(tenant, environment);
        if (!usableKeys(tenantId, environment, clock.instant()).isEmpty()) {
            throw TenantOperationException.conflict(
                "ACTIVE_KEY_EXISTS",
                "Tenant " + tenantId + " already has a usable " + environment + " key; rotate it instead"
            );
        }
        return create(tenant, environment);
    }

    /**
     * Zero-downtime rotation: provisions Key B and leaves Key A valid until {@code now + gracePeriod} (or an explicit revoke).
     *
     * @param gracePeriod the dual-active window, or {@code null} for the configured default
     */
    @Transactional
    public KeyRotationResult rotate(Long tenantId, ApiEnvironment environment, Duration gracePeriod) {
        CorporateTenant tenant = lockTenant(tenantId);
        requireEligible(tenant, environment);
        Duration grace = validatedGrace(gracePeriod);
        Instant now = clock.instant();
        List<ApiKey> current = usableKeys(tenantId, environment, now);
        if (current.isEmpty()) {
            throw TenantOperationException.conflict(
                "NO_ACTIVE_KEY",
                "Tenant " + tenantId + " has no usable " + environment + " key to rotate; issue one instead"
            );
        }
        if (current.size() >= 2) {
            throw TenantOperationException.conflict(
                "ROTATION_IN_PROGRESS",
                "Tenant " +
                    tenantId +
                    " already has two usable " +
                    environment +
                    " keys; revoke the old key or wait for its grace period to end"
            );
        }
        Instant expiresAt = now.plus(grace);
        for (ApiKey previous : current) {
            previous.setGraceExpiresAt(expiresAt);
            keys.save(previous);
        }
        IssuedApiKey issued = create(tenant, environment);
        LOG.info("Rotated {} key for tenant {}: new key {}, previous keys valid until {}", environment, tenantId, issued.id(), expiresAt);
        return new KeyRotationResult(issued, current.stream().map(ApiKey::getId).toList(), expiresAt);
    }

    /** Revokes a key immediately. Idempotent: revoking an already revoked key is a no-op. */
    @Transactional
    public void revoke(Long keyId) {
        Long tenantId = keys.findTenantIdById(keyId).orElseThrow(() -> TenantOperationException.notFound("API key", keyId));
        lockTenant(tenantId);
        // Loaded only after the lock is held, so it reflects any rotation that just committed.
        ApiKey key = keys.findById(keyId).orElseThrow(() -> TenantOperationException.notFound("API key", keyId));
        revokeNow(key);
    }

    /** Revokes every key of a tenant. The caller must already hold the tenant lock (see {@code TenantLifecycleService}). */
    @Transactional
    public void revokeAllForTenant(Long tenantId) {
        keys.findNotRevokedByTenant(tenantId).forEach(this::revokeNow);
    }

    /**
     * Resolves a presented secret to a principal.
     *
     * @return empty when the key is malformed, unknown, revoked, inactive or past its grace period (HTTP 401); otherwise the
     *     principal plus whether the tenant may currently process requests (a denied decision is HTTP 403)
     */
    @Transactional(readOnly = true)
    public Optional<ApiKeyAuthResult> authenticate(String presentedSecret) {
        if (!ApiKeyGenerator.isWellFormed(presentedSecret)) {
            return Optional.empty();
        }
        Optional<ApiKey> found = keys.findByKeyHash(hasher.hash(presentedSecret));
        if (found.isEmpty()) {
            return Optional.empty();
        }
        ApiKey key = found.orElseThrow();
        if (!isUsable(key, clock.instant()) || key.getEnvironment() != ApiKeyGenerator.environmentOf(presentedSecret)) {
            return Optional.empty();
        }
        CorporateTenant tenant = key.getTenant();
        ApiKeyPrincipal principal = new ApiKeyPrincipal(key.getId(), tenant.getId(), key.getEnvironment());
        return Optional.of(new ApiKeyAuthResult(principal, TenantAccessGuard.decide(key.getEnvironment(), tenant.getStatus())));
    }

    /** Flags keys whose grace period has lapsed as inactive. Returns how many were changed. */
    @Transactional
    public int expireLapsedKeys() {
        int changed = keys.deactivateLapsed(clock.instant());
        if (changed > 0) {
            LOG.info("Deactivated {} API key(s) whose grace period lapsed", changed);
        }
        return changed;
    }

    private CorporateTenant lockTenant(Long tenantId) {
        return tenants.findByIdForUpdate(tenantId).orElseThrow(() -> TenantOperationException.notFound("Tenant", tenantId));
    }

    private void requireEligible(CorporateTenant tenant, ApiEnvironment environment) {
        TenantAccessDecision decision = TenantAccessGuard.decide(environment, tenant.getStatus());
        if (!decision.isAllowed()) {
            throw TenantOperationException.forbidden(
                decision.errorCode(),
                "Tenant " + tenant.getId() + " is " + tenant.getStatus() + "; " + environment + " keys cannot be issued or rotated"
            );
        }
    }

    private Duration validatedGrace(Duration requested) {
        Duration grace = requested == null ? settings.defaultGracePeriod() : requested;
        if (grace.compareTo(settings.minGracePeriod()) < 0 || grace.compareTo(settings.maxGracePeriod()) > 0) {
            throw TenantOperationException.badRequest(
                "GRACE_PERIOD_OUT_OF_RANGE",
                "Grace period must be between " + settings.minGracePeriod() + " and " + settings.maxGracePeriod()
            );
        }
        return grace;
    }

    private List<ApiKey> usableKeys(Long tenantId, ApiEnvironment environment, Instant now) {
        return keys
            .findActiveByTenantAndEnvironment(tenantId, environment)
            .stream()
            .filter(k -> isUsable(k, now))
            .toList();
    }

    private static boolean isUsable(ApiKey key, Instant now) {
        return (
            Boolean.TRUE.equals(key.getIsActive()) &&
            key.getRevokedAt() == null &&
            (key.getGraceExpiresAt() == null || key.getGraceExpiresAt().isAfter(now))
        );
    }

    private IssuedApiKey create(CorporateTenant tenant, ApiEnvironment environment) {
        String secret = generator.generateSecret(environment);
        ApiKey saved = keys.save(
            new ApiKey()
                .keyPrefix(ApiKeyGenerator.prefixOf(secret))
                .keyHash(hasher.hash(secret))
                .environment(environment)
                .isActive(true)
                .issuedAt(clock.instant())
                .tenant(tenant)
        );
        return new IssuedApiKey(saved.getId(), saved.getKeyPrefix(), environment, secret, saved.getIssuedAt());
    }

    private void revokeNow(ApiKey key) {
        if (key.getRevokedAt() == null) {
            key.setRevokedAt(clock.instant());
        }
        key.setIsActive(false);
        keys.save(key);
    }
}
