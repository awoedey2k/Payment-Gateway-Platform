package io.paymentgateway.core.extended.tenant.service.lifecycle;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.repository.ExtendedCorporateTenantRepository;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies guarded tenant status transitions and their side effects.
 *
 * <ul>
 *   <li>Every edge must be legal in {@link TenantStatusMachine}.
 *   <li>Any move to ACTIVE requires {@code kycStatus == APPROVED}, so neither a direct call nor suspend-then-reinstate can
 *       bypass verification.
 *   <li>First activation stamps {@code activatedAt}.
 *   <li>REJECTED and CLOSED revoke every API key permanently (spec: DEACTIVATED, "keys permanently revoked").
 * </ul>
 *
 * SUSPENDED deliberately leaves keys in place: the access guard refuses their requests, and reinstatement restores them.
 */
@Service
public class TenantLifecycleService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantLifecycleService.class);

    private final ExtendedCorporateTenantRepository tenants;
    private final ApiKeyService apiKeys;
    private final EntityManager entityManager;
    private final Clock clock;

    public TenantLifecycleService(
        ExtendedCorporateTenantRepository tenants,
        ApiKeyService apiKeys,
        EntityManager entityManager,
        Clock clock
    ) {
        this.tenants = tenants;
        this.apiKeys = apiKeys;
        this.entityManager = entityManager;
        this.clock = clock;
    }

    @Transactional
    public CorporateTenant transition(Long tenantId, TenantStatus target, String reason) {
        CorporateTenant tenant = tenants
            .findByIdForUpdate(tenantId)
            .orElseThrow(() -> TenantOperationException.notFound("Tenant", tenantId));
        applyTransition(tenant, target, reason);
        return tenant;
    }

    /**
     * Applies a transition to a tenant the caller has already locked with {@code findByIdForUpdate} in the current transaction.
     *
     * @throws IllegalStateException if the tenant is not pessimistically locked (a programming error)
     */
    @Transactional
    public void applyTransition(CorporateTenant tenant, TenantStatus target, String reason) {
        if (entityManager.getLockMode(tenant) != LockModeType.PESSIMISTIC_WRITE) {
            throw new IllegalStateException("Tenant " + tenant.getId() + " must be locked (findByIdForUpdate) before a status transition");
        }
        TenantStatus from = tenant.getStatus();
        TenantStatusMachine.requireTransition(from, target);
        if (target == TenantStatus.ACTIVE && tenant.getKycStatus() != KycStatus.APPROVED) {
            throw TenantOperationException.conflict(
                "KYC_NOT_APPROVED",
                "Tenant " + tenant.getId() + " cannot become ACTIVE while kycStatus is " + tenant.getKycStatus()
            );
        }
        if (target == TenantStatus.ACTIVE && tenant.getActivatedAt() == null) {
            tenant.setActivatedAt(clock.instant());
        }
        if (target == TenantStatus.REJECTED) {
            tenant.setKycStatus(KycStatus.REJECTED);
        }
        tenant.setStatus(target);
        tenants.save(tenant);
        if (target == TenantStatus.REJECTED || target == TenantStatus.CLOSED) {
            apiKeys.revokeAllForTenant(tenant.getId());
        }
        LOG.info("Tenant {} status {} -> {} ({})", tenant.getId(), from, target, reason);
    }
}
