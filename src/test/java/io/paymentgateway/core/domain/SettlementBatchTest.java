package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.SettlementBatchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SettlementBatchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SettlementBatch.class);
        SettlementBatch settlementBatch1 = getSettlementBatchSample1();
        SettlementBatch settlementBatch2 = new SettlementBatch();
        assertThat(settlementBatch1).isNotEqualTo(settlementBatch2);

        settlementBatch2.setId(settlementBatch1.getId());
        assertThat(settlementBatch1).isEqualTo(settlementBatch2);

        settlementBatch2 = getSettlementBatchSample2();
        assertThat(settlementBatch1).isNotEqualTo(settlementBatch2);
    }

    @Test
    void tenantTest() {
        SettlementBatch settlementBatch = getSettlementBatchRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        settlementBatch.setTenant(corporateTenantBack);
        assertThat(settlementBatch.getTenant()).isEqualTo(corporateTenantBack);

        settlementBatch.tenant(null);
        assertThat(settlementBatch.getTenant()).isNull();
    }
}
