package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LedgerAccountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LedgerAccountDTO.class);
        LedgerAccountDTO ledgerAccountDTO1 = new LedgerAccountDTO();
        ledgerAccountDTO1.setId(1L);
        LedgerAccountDTO ledgerAccountDTO2 = new LedgerAccountDTO();
        assertThat(ledgerAccountDTO1).isNotEqualTo(ledgerAccountDTO2);
        ledgerAccountDTO2.setId(ledgerAccountDTO1.getId());
        assertThat(ledgerAccountDTO1).isEqualTo(ledgerAccountDTO2);
        ledgerAccountDTO2.setId(2L);
        assertThat(ledgerAccountDTO1).isNotEqualTo(ledgerAccountDTO2);
        ledgerAccountDTO1.setId(null);
        assertThat(ledgerAccountDTO1).isNotEqualTo(ledgerAccountDTO2);
    }
}
