package io.paymentgateway.core.extended.tenant.service;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.domain.TenantAccessDecision;
import io.paymentgateway.core.extended.tenant.repository.ExtendedCorporateTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Decides whether a tenant may process requests. API-key authentication uses {@link #decide} on every request, and the
 * Transaction module (Chunk 4) calls {@link #assertCanTransact} before creating a charge so a suspended tenant cannot
 * start new transactions.
 *
 * <p>ACTIVE: everything. PENDING_REVIEW: sandbox (TEST) only, LIVE is refused with {@code TENANT_NOT_VERIFIED}.
 * SUSPENDED / REJECTED / CLOSED: nothing, in either environment.
 */
@Service
public class TenantAccessGuard {

    private final ExtendedCorporateTenantRepository tenants;

    public TenantAccessGuard(ExtendedCorporateTenantRepository tenants) {
        this.tenants = tenants;
    }

    public static TenantAccessDecision decide(ApiEnvironment environment, TenantStatus status) {
        return switch (status) {
            case ACTIVE -> TenantAccessDecision.ALLOWED;
            case PENDING_REVIEW -> environment == ApiEnvironment.TEST
                ? TenantAccessDecision.ALLOWED
                : TenantAccessDecision.TENANT_NOT_VERIFIED;
            case SUSPENDED -> TenantAccessDecision.TENANT_SUSPENDED;
            case REJECTED -> TenantAccessDecision.TENANT_REJECTED;
            case CLOSED -> TenantAccessDecision.TENANT_CLOSED;
        };
    }

    /** @throws TenantOperationException HTTP 403 with the decision's error code when the tenant may not transact. */
    @Transactional(readOnly = true)
    public void assertCanTransact(Long tenantId, ApiEnvironment environment) {
        TenantStatus status = tenants
            .findById(tenantId)
            .orElseThrow(() -> TenantOperationException.notFound("Tenant", tenantId))
            .getStatus();
        TenantAccessDecision decision = decide(environment, status);
        if (!decision.isAllowed()) {
            throw TenantOperationException.forbidden(
                decision.errorCode(),
                "Tenant " + tenantId + " is " + status + "; " + environment + " requests are refused"
            );
        }
    }
}
