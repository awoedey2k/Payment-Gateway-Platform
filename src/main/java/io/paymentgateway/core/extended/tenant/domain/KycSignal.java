package io.paymentgateway.core.extended.tenant.domain;

/** A discrete finding from sanctions/PEP screening or the business-registry cross-reference. */
public enum KycSignal {
    SANCTIONS_HIT,
    PEP_HIT,
    REGISTRY_DISSOLVED,
    REGISTRY_NOT_FOUND,
    REGISTRY_UNAVAILABLE,
    NAME_MISMATCH,
    NO_DIRECTORS,
    SCREENING_UNAVAILABLE,
}
