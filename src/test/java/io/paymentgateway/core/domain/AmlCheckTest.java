package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.AmlCheckTestSamples.*;
import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmlCheckTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmlCheck.class);
        AmlCheck amlCheck1 = getAmlCheckSample1();
        AmlCheck amlCheck2 = new AmlCheck();
        assertThat(amlCheck1).isNotEqualTo(amlCheck2);

        amlCheck2.setId(amlCheck1.getId());
        assertThat(amlCheck1).isEqualTo(amlCheck2);

        amlCheck2 = getAmlCheckSample2();
        assertThat(amlCheck1).isNotEqualTo(amlCheck2);
    }

    @Test
    void tenantTest() {
        AmlCheck amlCheck = getAmlCheckRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        amlCheck.setTenant(corporateTenantBack);
        assertThat(amlCheck.getTenant()).isEqualTo(corporateTenantBack);

        amlCheck.tenant(null);
        assertThat(amlCheck.getTenant()).isNull();
    }

    @Test
    void transactionTest() {
        AmlCheck amlCheck = getAmlCheckRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        amlCheck.setTransaction(transactionBack);
        assertThat(amlCheck.getTransaction()).isEqualTo(transactionBack);

        amlCheck.transaction(null);
        assertThat(amlCheck.getTransaction()).isNull();
    }
}
