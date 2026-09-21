package io.paymentgateway.core.extended.tenant.service.kyc;

public record SanctionsScreeningResult(boolean sanctionsHit, boolean pepHit) {
    public static final SanctionsScreeningResult CLEAR = new SanctionsScreeningResult(false, false);
}
