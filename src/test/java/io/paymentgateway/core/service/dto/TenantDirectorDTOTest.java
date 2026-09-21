package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantDirectorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantDirectorDTO.class);
        TenantDirectorDTO tenantDirectorDTO1 = new TenantDirectorDTO();
        tenantDirectorDTO1.setId(1L);
        TenantDirectorDTO tenantDirectorDTO2 = new TenantDirectorDTO();
        assertThat(tenantDirectorDTO1).isNotEqualTo(tenantDirectorDTO2);
        tenantDirectorDTO2.setId(tenantDirectorDTO1.getId());
        assertThat(tenantDirectorDTO1).isEqualTo(tenantDirectorDTO2);
        tenantDirectorDTO2.setId(2L);
        assertThat(tenantDirectorDTO1).isNotEqualTo(tenantDirectorDTO2);
        tenantDirectorDTO1.setId(null);
        assertThat(tenantDirectorDTO1).isNotEqualTo(tenantDirectorDTO2);
    }
}
