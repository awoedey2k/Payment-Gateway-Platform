package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.TenantDomainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantDomainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantDomain.class);
        TenantDomain tenantDomain1 = getTenantDomainSample1();
        TenantDomain tenantDomain2 = new TenantDomain();
        assertThat(tenantDomain1).isNotEqualTo(tenantDomain2);

        tenantDomain2.setId(tenantDomain1.getId());
        assertThat(tenantDomain1).isEqualTo(tenantDomain2);

        tenantDomain2 = getTenantDomainSample2();
        assertThat(tenantDomain1).isNotEqualTo(tenantDomain2);
    }

    @Test
    void tenantTest() {
        TenantDomain tenantDomain = getTenantDomainRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        tenantDomain.setTenant(corporateTenantBack);
        assertThat(tenantDomain.getTenant()).isEqualTo(corporateTenantBack);

        tenantDomain.tenant(null);
        assertThat(tenantDomain.getTenant()).isNull();
    }
}
