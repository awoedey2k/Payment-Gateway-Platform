package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantDomainDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantDomainDTO.class);
        TenantDomainDTO tenantDomainDTO1 = new TenantDomainDTO();
        tenantDomainDTO1.setId(1L);
        TenantDomainDTO tenantDomainDTO2 = new TenantDomainDTO();
        assertThat(tenantDomainDTO1).isNotEqualTo(tenantDomainDTO2);
        tenantDomainDTO2.setId(tenantDomainDTO1.getId());
        assertThat(tenantDomainDTO1).isEqualTo(tenantDomainDTO2);
        tenantDomainDTO2.setId(2L);
        assertThat(tenantDomainDTO1).isNotEqualTo(tenantDomainDTO2);
        tenantDomainDTO1.setId(null);
        assertThat(tenantDomainDTO1).isNotEqualTo(tenantDomainDTO2);
    }
}
