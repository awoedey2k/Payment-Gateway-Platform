package io.paymentgateway.core.extended.tenant.service.apikey;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/** Generates and recognises secret keys of the form {@code sk_live_<64 hex>} / {@code sk_test_<64 hex>}. */
public final class ApiKeyGenerator {

    /** Length of the display prefix stored with each key: {@code sk_live_} plus four secret characters, as in the spec example. */
    public static final int PREFIX_LENGTH = 12;

    private static final Pattern FORMAT = Pattern.compile("^sk_(test|live)_[0-9a-f]{64}$");

    private final SecureRandom random = new SecureRandom();

    public String generateSecret(ApiEnvironment environment) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "sk_" + environment.name().toLowerCase(Locale.ROOT) + "_" + HexFormat.of().formatHex(bytes);
    }

    public static String prefixOf(String secret) {
        return secret.substring(0, PREFIX_LENGTH);
    }

    /** Cheap syntactic check so garbage is rejected before hashing or touching the database. */
    public static boolean isWellFormed(String presented) {
        return presented != null && FORMAT.matcher(presented).matches();
    }

    public static ApiEnvironment environmentOf(String wellFormedSecret) {
        return wellFormedSecret.startsWith("sk_live_") ? ApiEnvironment.LIVE : ApiEnvironment.TEST;
    }
}
