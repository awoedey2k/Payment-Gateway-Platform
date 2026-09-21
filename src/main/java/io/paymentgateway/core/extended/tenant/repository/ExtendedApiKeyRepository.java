package io.paymentgateway.core.extended.tenant.repository;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Extra queries for {@link ApiKey}; see {@link ExtendedCorporateTenantRepository} for why it does not extend the generated one. */
public interface ExtendedApiKeyRepository extends JpaRepository<ApiKey, Long> {
    /** The single indexed lookup used on every authenticated request (keyHash is unique). Never served from a cache. */
    @Query(
        "select new io.paymentgateway.core.extended.tenant.repository.ApiKeyAuthRow(" +
            "k.id, k.environment, k.isActive, k.revokedAt, k.graceExpiresAt, t.id, t.status) " +
            "from ApiKey k join k.tenant t where k.keyHash = :keyHash"
    )
    Optional<ApiKeyAuthRow> findAuthRowByKeyHash(@Param("keyHash") String keyHash);

    @Query(
        "select k from ApiKey k where k.tenant.id = :tenantId and k.environment = :environment " +
            "and k.isActive = true and k.revokedAt is null"
    )
    List<ApiKey> findActiveByTenantAndEnvironment(@Param("tenantId") Long tenantId, @Param("environment") ApiEnvironment environment);

    @Query("select k from ApiKey k where k.tenant.id = :tenantId and k.revokedAt is null")
    List<ApiKey> findNotRevokedByTenant(@Param("tenantId") Long tenantId);

    /** Tenant id of a key without loading the entity, so the caller can take the tenant lock before loading it. */
    @Query("select k.tenant.id from ApiKey k where k.id = :id")
    Optional<Long> findTenantIdById(@Param("id") Long id);

    /**
     * Deactivates keys whose grace period has lapsed. A single bulk statement, so it can never overwrite a concurrent
     * revoke with a stale row (Hibernate also invalidates the ApiKey cache region for HQL bulk updates).
     */
    @Modifying(clearAutomatically = true)
    @Query(
        "update ApiKey k set k.isActive = false where k.isActive = true and k.revokedAt is null " +
            "and k.graceExpiresAt is not null and k.graceExpiresAt <= :now"
    )
    int deactivateLapsed(@Param("now") Instant now);
}
