package io.paymentgateway.core.extended.tenant.service.lifecycle;

import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * The tenant status state machine (spec Chunk 2 §1.1, using the JDL's status names).
 *
 * <pre>
 * PENDING_REVIEW -> ACTIVE | SUSPENDED | REJECTED
 * ACTIVE         -> SUSPENDED | CLOSED
 * SUSPENDED      -> ACTIVE | CLOSED
 * REJECTED, CLOSED are terminal
 * </pre>
 *
 * <p>Spec names: PENDING_VERIFICATION = PENDING_REVIEW, DEACTIVATED = CLOSED. This class only answers "is this edge
 * legal"; the KYC guard on moves to ACTIVE lives in {@code TenantLifecycleService}.
 */
public final class TenantStatusMachine {

    private static final Map<TenantStatus, Set<TenantStatus>> ALLOWED = new EnumMap<>(TenantStatus.class);

    static {
        ALLOWED.put(TenantStatus.PENDING_REVIEW, Set.of(TenantStatus.ACTIVE, TenantStatus.SUSPENDED, TenantStatus.REJECTED));
        ALLOWED.put(TenantStatus.ACTIVE, Set.of(TenantStatus.SUSPENDED, TenantStatus.CLOSED));
        ALLOWED.put(TenantStatus.SUSPENDED, Set.of(TenantStatus.ACTIVE, TenantStatus.CLOSED));
        ALLOWED.put(TenantStatus.REJECTED, Set.of());
        ALLOWED.put(TenantStatus.CLOSED, Set.of());
    }

    private TenantStatusMachine() {}

    public static Set<TenantStatus> allowedFrom(TenantStatus from) {
        return ALLOWED.get(from);
    }

    public static boolean canTransition(TenantStatus from, TenantStatus to) {
        return ALLOWED.get(from).contains(to);
    }

    /** @throws TenantOperationException (HTTP 409, {@code INVALID_TENANT_TRANSITION}) when the edge is not allowed. */
    public static void requireTransition(TenantStatus from, TenantStatus to) {
        if (!canTransition(from, to)) {
            throw TenantOperationException.conflict(
                "INVALID_TENANT_TRANSITION",
                "Tenant cannot move from " + from + " to " + to + "; allowed from " + from + ": " + ALLOWED.get(from)
            );
        }
    }
}
