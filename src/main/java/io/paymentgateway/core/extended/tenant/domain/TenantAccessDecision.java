package io.paymentgateway.core.extended.tenant.domain;

/** Whether a tenant in its current status may process requests in a given environment. */
public enum TenantAccessDecision {
    ALLOWED(null),
    /** Live processing before verification completes (spec: HTTP 403 {@code TENANT_NOT_VERIFIED}). */
    TENANT_NOT_VERIFIED("TENANT_NOT_VERIFIED"),
    TENANT_SUSPENDED("TENANT_SUSPENDED"),
    TENANT_REJECTED("TENANT_REJECTED"),
    TENANT_CLOSED("TENANT_CLOSED");

    private final String errorCode;

    TenantAccessDecision(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isAllowed() {
        return this == ALLOWED;
    }

    /** Machine-readable error code returned to the caller, or {@code null} when allowed. */
    public String errorCode() {
        return errorCode;
    }
}
