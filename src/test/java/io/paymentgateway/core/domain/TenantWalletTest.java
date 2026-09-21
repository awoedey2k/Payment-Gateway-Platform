package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.TenantWalletTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantWalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantWallet.class);
        TenantWallet tenantWallet1 = getTenantWalletSample1();
        TenantWallet tenantWallet2 = new TenantWallet();
        assertThat(tenantWallet1).isNotEqualTo(tenantWallet2);

        tenantWallet2.setId(tenantWallet1.getId());
        assertThat(tenantWallet1).isEqualTo(tenantWallet2);

        tenantWallet2 = getTenantWalletSample2();
        assertThat(tenantWallet1).isNotEqualTo(tenantWallet2);
    }

    @Test
    void tenantTest() {
        TenantWallet tenantWallet = getTenantWalletRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        tenantWallet.setTenant(corporateTenantBack);
        assertThat(tenantWallet.getTenant()).isEqualTo(corporateTenantBack);

        tenantWallet.tenant(null);
        assertThat(tenantWallet.getTenant()).isNull();
    }
}
