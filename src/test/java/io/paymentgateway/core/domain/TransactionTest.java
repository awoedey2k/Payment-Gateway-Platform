package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.RefundTestSamples.*;
import static io.paymentgateway.core.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = getTransactionSample1();
        Transaction transaction2 = new Transaction();
        assertThat(transaction1).isNotEqualTo(transaction2);

        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);

        transaction2 = getTransactionSample2();
        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void refundTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Refund refundBack = getRefundRandomSampleGenerator();

        transaction.addRefund(refundBack);
        assertThat(transaction.getRefunds()).containsOnly(refundBack);
        assertThat(refundBack.getTransaction()).isEqualTo(transaction);

        transaction.removeRefund(refundBack);
        assertThat(transaction.getRefunds()).doesNotContain(refundBack);
        assertThat(refundBack.getTransaction()).isNull();

        transaction.refunds(new HashSet<>(Set.of(refundBack)));
        assertThat(transaction.getRefunds()).containsOnly(refundBack);
        assertThat(refundBack.getTransaction()).isEqualTo(transaction);

        transaction.setRefunds(new HashSet<>());
        assertThat(transaction.getRefunds()).doesNotContain(refundBack);
        assertThat(refundBack.getTransaction()).isNull();
    }

    @Test
    void tenantTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        transaction.setTenant(corporateTenantBack);
        assertThat(transaction.getTenant()).isEqualTo(corporateTenantBack);

        transaction.tenant(null);
        assertThat(transaction.getTenant()).isNull();
    }
}
