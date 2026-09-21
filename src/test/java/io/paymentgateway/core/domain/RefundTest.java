package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.RefundTestSamples.*;
import static io.paymentgateway.core.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Refund.class);
        Refund refund1 = getRefundSample1();
        Refund refund2 = new Refund();
        assertThat(refund1).isNotEqualTo(refund2);

        refund2.setId(refund1.getId());
        assertThat(refund1).isEqualTo(refund2);

        refund2 = getRefundSample2();
        assertThat(refund1).isNotEqualTo(refund2);
    }

    @Test
    void transactionTest() {
        Refund refund = getRefundRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        refund.setTransaction(transactionBack);
        assertThat(refund.getTransaction()).isEqualTo(transactionBack);

        refund.transaction(null);
        assertThat(refund.getTransaction()).isNull();
    }
}
