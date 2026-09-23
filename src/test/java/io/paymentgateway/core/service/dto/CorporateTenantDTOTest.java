package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CorporateTenantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CorporateTenantDTO.class);
        CorporateTenantDTO corporateTenantDTO1 = new CorporateTenantDTO();
        corporateTenantDTO1.setId(1L);
        CorporateTenantDTO corporateTenantDTO2 = new CorporateTenantDTO();
        assertThat(corporateTenantDTO1).isNotEqualTo(corporateTenantDTO2);
        corporateTenantDTO2.setId(corporateTenantDTO1.getId());
        assertThat(corporateTenantDTO1).isEqualTo(corporateTenantDTO2);
        corporateTenantDTO2.setId(2L);
        assertThat(corporateTenantDTO1).isNotEqualTo(corporateTenantDTO2);
        corporateTenantDTO1.setId(null);
        assertThat(corporateTenantDTO1).isNotEqualTo(corporateTenantDTO2);
    }
}
