package io.paymentgateway.core.extended.tenant.domain;

/** Outcome of the automated scoring bands (spec Chunk 2 §2.2, Figure 2.2). */
public enum KycDecision {
    /** Score 0-20: instant approval. */
    APPROVE,
    /** Score 21-60: Tier-2 manual compliance review. */
    MANUAL_REVIEW,
    /** Score 61-100: immediate rejection. */
    REJECT,
}
