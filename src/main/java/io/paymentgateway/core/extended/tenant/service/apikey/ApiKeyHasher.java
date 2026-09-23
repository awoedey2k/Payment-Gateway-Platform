package io.paymentgateway.core.extended.tenant.service.apikey;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * One-way hash for API secrets: HMAC-SHA-256 keyed with a server-side pepper, hex encoded.
 *
 * <p>Secrets are 256-bit random values, so a fast keyed hash is appropriate (a slow password hash would only add per-request
 * latency). The pepper is never stored with the hashes, so a leaked table alone cannot be used to test guesses.
 */
public final class ApiKeyHasher {

    private static final String ALGORITHM = "HmacSHA256";

    private final byte[] pepper;

    public ApiKeyHasher(String pepper) {
        this.pepper = pepper.getBytes(StandardCharsets.UTF_8);
    }

    public String hash(String secret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(pepper, ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 is unavailable", e);
        }
    }
}
