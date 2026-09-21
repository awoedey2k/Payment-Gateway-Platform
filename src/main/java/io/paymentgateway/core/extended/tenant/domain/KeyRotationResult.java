package io.paymentgateway.core.extended.tenant.domain;

import java.time.Instant;
import java.util.List;

/** Outcome of a rotation: the new key (B) and when the previous key(s) (A) stop authenticating unless revoked sooner. */
public record KeyRotationResult(IssuedApiKey newKey, List<Long> previousKeyIds, Instant previousKeysExpireAt) {}
