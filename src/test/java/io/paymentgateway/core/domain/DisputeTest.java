package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.DisputeTestSamples.*;
import static io.paymentgateway.core.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisputeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dispute.class);
        Dispute dispute1 = getDisputeSample1();
        Dispute dispute2 = new Dispute();
        assertThat(dispute1).isNotEqualTo(dispute2);

        dispute2.setId(dispute1.getId());
        assertThat(dispute1).isEqualTo(dispute2);

        dispute2 = getDisputeSample2();
        assertThat(dispute1).isNotEqualTo(dispute2);
    }

    @Test
    void tenantTest() {
        Dispute dispute = getDisputeRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        dispute.setTenant(corporateTenantBack);
        assertThat(dispute.getTenant()).isEqualTo(corporateTenantBack);

        dispute.tenant(null);
        assertThat(dispute.getTenant()).isNull();
    }

    @Test
    void transactionTest() {
        Dispute dispute = getDisputeRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        dispute.setTransaction(transactionBack);
        assertThat(dispute.getTransaction()).isEqualTo(transactionBack);

        dispute.transaction(null);
        assertThat(dispute.getTransaction()).isNull();
    }
}
