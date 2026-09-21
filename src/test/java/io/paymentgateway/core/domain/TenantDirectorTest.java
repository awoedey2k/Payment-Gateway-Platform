package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.TenantDirectorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantDirectorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantDirector.class);
        TenantDirector tenantDirector1 = getTenantDirectorSample1();
        TenantDirector tenantDirector2 = new TenantDirector();
        assertThat(tenantDirector1).isNotEqualTo(tenantDirector2);

        tenantDirector2.setId(tenantDirector1.getId());
        assertThat(tenantDirector1).isEqualTo(tenantDirector2);

        tenantDirector2 = getTenantDirectorSample2();
        assertThat(tenantDirector1).isNotEqualTo(tenantDirector2);
    }

    @Test
    void tenantTest() {
        TenantDirector tenantDirector = getTenantDirectorRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        tenantDirector.setTenant(corporateTenantBack);
        assertThat(tenantDirector.getTenant()).isEqualTo(corporateTenantBack);

        tenantDirector.tenant(null);
        assertThat(tenantDirector.getTenant()).isNull();
    }
}
