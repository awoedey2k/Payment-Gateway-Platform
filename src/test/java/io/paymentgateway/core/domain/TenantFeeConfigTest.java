package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.CountryPaymentMethodTestSamples.*;
import static io.paymentgateway.core.domain.TenantFeeConfigTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantFeeConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantFeeConfig.class);
        TenantFeeConfig tenantFeeConfig1 = getTenantFeeConfigSample1();
        TenantFeeConfig tenantFeeConfig2 = new TenantFeeConfig();
        assertThat(tenantFeeConfig1).isNotEqualTo(tenantFeeConfig2);

        tenantFeeConfig2.setId(tenantFeeConfig1.getId());
        assertThat(tenantFeeConfig1).isEqualTo(tenantFeeConfig2);

        tenantFeeConfig2 = getTenantFeeConfigSample2();
        assertThat(tenantFeeConfig1).isNotEqualTo(tenantFeeConfig2);
    }

    @Test
    void tenantTest() {
        TenantFeeConfig tenantFeeConfig = getTenantFeeConfigRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        tenantFeeConfig.setTenant(corporateTenantBack);
        assertThat(tenantFeeConfig.getTenant()).isEqualTo(corporateTenantBack);

        tenantFeeConfig.tenant(null);
        assertThat(tenantFeeConfig.getTenant()).isNull();
    }

    @Test
    void countryPaymentMethodTest() {
        TenantFeeConfig tenantFeeConfig = getTenantFeeConfigRandomSampleGenerator();
        CountryPaymentMethod countryPaymentMethodBack = getCountryPaymentMethodRandomSampleGenerator();

        tenantFeeConfig.setCountryPaymentMethod(countryPaymentMethodBack);
        assertThat(tenantFeeConfig.getCountryPaymentMethod()).isEqualTo(countryPaymentMethodBack);

        tenantFeeConfig.countryPaymentMethod(null);
        assertThat(tenantFeeConfig.getCountryPaymentMethod()).isNull();
    }
}
