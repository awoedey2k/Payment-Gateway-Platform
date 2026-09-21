package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.LedgerAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LedgerAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LedgerAccount.class);
        LedgerAccount ledgerAccount1 = getLedgerAccountSample1();
        LedgerAccount ledgerAccount2 = new LedgerAccount();
        assertThat(ledgerAccount1).isNotEqualTo(ledgerAccount2);

        ledgerAccount2.setId(ledgerAccount1.getId());
        assertThat(ledgerAccount1).isEqualTo(ledgerAccount2);

        ledgerAccount2 = getLedgerAccountSample2();
        assertThat(ledgerAccount1).isNotEqualTo(ledgerAccount2);
    }
}
