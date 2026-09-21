package io.paymentgateway.core.extended.tenant.domain;

import java.util.Set;

/** Result of scoring one KYB submission. */
public record KycAssessment(int score, Set<KycSignal> signals, KycDecision decision) {
    public KycAssessment {
        signals = Set.copyOf(signals);
    }
}
