package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantWalletDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantWalletDTO.class);
        TenantWalletDTO tenantWalletDTO1 = new TenantWalletDTO();
        tenantWalletDTO1.setId(1L);
        TenantWalletDTO tenantWalletDTO2 = new TenantWalletDTO();
        assertThat(tenantWalletDTO1).isNotEqualTo(tenantWalletDTO2);
        tenantWalletDTO2.setId(tenantWalletDTO1.getId());
        assertThat(tenantWalletDTO1).isEqualTo(tenantWalletDTO2);
        tenantWalletDTO2.setId(2L);
        assertThat(tenantWalletDTO1).isNotEqualTo(tenantWalletDTO2);
        tenantWalletDTO1.setId(null);
        assertThat(tenantWalletDTO1).isNotEqualTo(tenantWalletDTO2);
    }
}
