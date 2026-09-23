package io.paymentgateway.core.extended.tenant.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.paymentgateway.core.extended.tenant.support.TestTenantProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Production must not start with the development pepper or the stub KYC provider. */
class TenantConfigurationTest {

    private static final String GOOD_PEPPER = "0123456789abcdef0123456789abcdef";

    private static Map<String, String> prodReady() {
        return Map.of("payment-gateway.tenant.api-keys.pepper", GOOD_PEPPER, "payment-gateway.tenant.kyc.provider", "complyadvantage");
    }

    @Test
    void nonProductionAcceptsDevelopmentDefaults() {
        assertThatCode(() ->
            TenantConfiguration.requireSafeForEnvironment(TestTenantProperties.defaults(), false)
        ).doesNotThrowAnyException();
    }

    @Test
    void productionAcceptsAStrongPepperAndARealProvider() {
        assertThatCode(() ->
            TenantConfiguration.requireSafeForEnvironment(TestTenantProperties.with(prodReady()), true)
        ).doesNotThrowAnyException();
    }

    @Test
    void productionRefusesTheDevelopmentPepper() {
        assertThatThrownBy(() ->
            TenantConfiguration.requireSafeForEnvironment(
                TestTenantProperties.with(Map.of("payment-gateway.tenant.kyc.provider", "complyadvantage")),
                true
            )
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("pepper");
    }

    @Test
    void productionRefusesAShortPepper() {
        assertThatThrownBy(() ->
            TenantConfiguration.requireSafeForEnvironment(
                TestTenantProperties.with(
                    Map.of("payment-gateway.tenant.api-keys.pepper", "too-short", "payment-gateway.tenant.kyc.provider", "complyadvantage")
                ),
                true
            )
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("pepper");
    }

    @Test
    void productionRefusesTheStubKycProvider() {
        assertThatThrownBy(() ->
            TenantConfiguration.requireSafeForEnvironment(
                TestTenantProperties.with(Map.of("payment-gateway.tenant.api-keys.pepper", GOOD_PEPPER)),
                true
            )
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("stub");
    }

    @Test
    void onlyDevAndTestProfilesAreTreatedAsNonProduction() {
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "dev", "api-docs" })).isTrue();
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "testdev" })).isTrue();
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "testprod" })).isTrue();
        // Fail closed for every named environment that is not dev/test.
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "prod" })).isFalse();
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "staging" })).isFalse();
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "prod", "cloud" })).isFalse();
        // No profile at all is development: JHipster builds always set one and the generated Cucumber tests run without any.
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] {})).isTrue();
        assertThat(TenantConfiguration.isDevelopmentOrTest(new String[] { "prod", "dev" })).isTrue(); // an explicit dev opt-in wins
    }

    @Test
    void defaultWeightsAreValid() {
        assertThatCode(() ->
            TenantConfiguration.requireValidWeights(TestTenantProperties.defaults().kyc().weights())
        ).doesNotThrowAnyException();
    }

    @Test
    void aWeightAtOrBelowTheAutoApproveCeilingIsRefusedAtStartup() {
        for (String weight : new String[] { "20", "0", "-5" }) {
            assertThatThrownBy(() ->
                TenantConfiguration.requireValidWeights(
                    TestTenantProperties.with(Map.of("payment-gateway.tenant.kyc.weights.pep-hit", weight)).kyc().weights()
                )
            )
                .as("weight %s", weight)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pep-hit");
        }
    }

    @Test
    void aWeightAboveOneHundredIsRefused() {
        assertThatThrownBy(() ->
            TenantConfiguration.requireValidWeights(
                TestTenantProperties.with(Map.of("payment-gateway.tenant.kyc.weights.no-directors", "101")).kyc().weights()
            )
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no-directors");
    }

    @Test
    void theBoundaryWeightsTwentyOneAndOneHundredAreAccepted() {
        assertThatCode(() ->
            TenantConfiguration.requireValidWeights(
                TestTenantProperties.with(
                    Map.of("payment-gateway.tenant.kyc.weights.pep-hit", "21", "payment-gateway.tenant.kyc.weights.sanctions-hit", "100")
                )
                    .kyc()
                    .weights()
            )
        ).doesNotThrowAnyException();
    }
}
