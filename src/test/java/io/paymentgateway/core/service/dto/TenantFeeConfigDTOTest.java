package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantFeeConfigDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantFeeConfigDTO.class);
        TenantFeeConfigDTO tenantFeeConfigDTO1 = new TenantFeeConfigDTO();
        tenantFeeConfigDTO1.setId(1L);
        TenantFeeConfigDTO tenantFeeConfigDTO2 = new TenantFeeConfigDTO();
        assertThat(tenantFeeConfigDTO1).isNotEqualTo(tenantFeeConfigDTO2);
        tenantFeeConfigDTO2.setId(tenantFeeConfigDTO1.getId());
        assertThat(tenantFeeConfigDTO1).isEqualTo(tenantFeeConfigDTO2);
        tenantFeeConfigDTO2.setId(2L);
        assertThat(tenantFeeConfigDTO1).isNotEqualTo(tenantFeeConfigDTO2);
        tenantFeeConfigDTO1.setId(null);
        assertThat(tenantFeeConfigDTO1).isNotEqualTo(tenantFeeConfigDTO2);
    }
}
