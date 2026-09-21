package io.paymentgateway.core.extended.tenant.config;

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
}
