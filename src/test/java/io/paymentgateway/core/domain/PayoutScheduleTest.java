package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.PayoutScheduleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PayoutScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayoutSchedule.class);
        PayoutSchedule payoutSchedule1 = getPayoutScheduleSample1();
        PayoutSchedule payoutSchedule2 = new PayoutSchedule();
        assertThat(payoutSchedule1).isNotEqualTo(payoutSchedule2);

        payoutSchedule2.setId(payoutSchedule1.getId());
        assertThat(payoutSchedule1).isEqualTo(payoutSchedule2);

        payoutSchedule2 = getPayoutScheduleSample2();
        assertThat(payoutSchedule1).isNotEqualTo(payoutSchedule2);
    }

    @Test
    void tenantTest() {
        PayoutSchedule payoutSchedule = getPayoutScheduleRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        payoutSchedule.setTenant(corporateTenantBack);
        assertThat(payoutSchedule.getTenant()).isEqualTo(corporateTenantBack);

        payoutSchedule.tenant(null);
        assertThat(payoutSchedule.getTenant()).isNull();
    }
}
