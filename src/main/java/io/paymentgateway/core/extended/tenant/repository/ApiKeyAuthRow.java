package io.paymentgateway.core.extended.tenant.repository;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import java.time.Instant;

/**
 * Exactly the columns authentication needs, read with one query. Deliberately a projection rather than the {@code ApiKey} /
 * {@code CorporateTenant} entities: entities can be served from the Hibernate second-level cache, which is per node, so a
 * suspension or revocation on one node could be missed by another for the cache TTL. A query always reads the database.
 */
public record ApiKeyAuthRow(
    Long keyId,
    ApiEnvironment environment,
    Boolean isActive,
    Instant revokedAt,
    Instant graceExpiresAt,
    Long tenantId,
    TenantStatus tenantStatus
) {}
