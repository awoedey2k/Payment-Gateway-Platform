package io.paymentgateway.core.extended.tenant.service.apikey;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.extended.tenant.domain.IssuedApiKey;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ApiKeyHasherAndGeneratorTest {

    private final ApiKeyGenerator generator = new ApiKeyGenerator();

    @Test
    void hashIsDeterministicHexAndDoesNotContainTheSecret() {
        ApiKeyHasher hasher = new ApiKeyHasher("pepper-one");
        String secret = generator.generateSecret(ApiEnvironment.LIVE);
        String hash = hasher.hash(secret);
        assertThat(hash).isEqualTo(hasher.hash(secret)).matches("[0-9a-f]{64}").doesNotContain(secret.substring(8));
    }

    @Test
    void differentPeppersGiveDifferentHashesSoALeakedTableCannotBeCheckedWithoutThePepper() {
        String secret = generator.generateSecret(ApiEnvironment.TEST);
        assertThat(new ApiKeyHasher("pepper-one").hash(secret)).isNotEqualTo(new ApiKeyHasher("pepper-two").hash(secret));
    }

    @Test
    void differentSecretsGiveDifferentHashes() {
        ApiKeyHasher hasher = new ApiKeyHasher("p");
        assertThat(hasher.hash(generator.generateSecret(ApiEnvironment.LIVE))).isNotEqualTo(
            hasher.hash(generator.generateSecret(ApiEnvironment.LIVE))
        );
    }

    @Test
    void secretsHaveTheDocumentedShapeAndEnvironment() {
        String live = generator.generateSecret(ApiEnvironment.LIVE);
        String test = generator.generateSecret(ApiEnvironment.TEST);
        assertThat(live).matches("sk_live_[0-9a-f]{64}");
        assertThat(test).matches("sk_test_[0-9a-f]{64}");
        assertThat(ApiKeyGenerator.environmentOf(live)).isEqualTo(ApiEnvironment.LIVE);
        assertThat(ApiKeyGenerator.environmentOf(test)).isEqualTo(ApiEnvironment.TEST);
        assertThat(ApiKeyGenerator.prefixOf(live)).hasSize(12).isEqualTo(live.substring(0, 12)).startsWith("sk_live_");
    }

    @Test
    void secretsDoNotRepeat() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1_000; i++) {
            assertThat(seen.add(generator.generateSecret(ApiEnvironment.LIVE))).isTrue();
        }
    }

    static Stream<String> malformedSecrets() {
        String hex = "a".repeat(64);
        return Stream.of(
            null,
            "",
            "sk_live_",
            "sk_live_abc",
            "pk_live_" + hex,
            "sk_prod_" + hex,
            "sk_live_" + "A".repeat(64),
            "sk_live_" + "g".repeat(64),
            "sk_live_" + "a".repeat(65),
            " sk_live_" + hex,
            "sk_live_" + hex + "\n"
        );
    }

    @ParameterizedTest
    @MethodSource("malformedSecrets")
    void malformedSecretsAreRejectedBeforeAnyLookup(String presented) {
        assertThat(ApiKeyGenerator.isWellFormed(presented)).isFalse();
    }

    @Test
    void issuedKeyToStringNeverRevealsTheSecret() {
        IssuedApiKey key = new IssuedApiKey(7L, "sk_live_21f9", ApiEnvironment.LIVE, "sk_live_TOPSECRET", Instant.EPOCH);
        assertThat(key.toString()).doesNotContain("TOPSECRET").contains("id=7");
    }
}
