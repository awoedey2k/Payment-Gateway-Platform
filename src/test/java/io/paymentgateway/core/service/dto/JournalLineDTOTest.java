package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalLineDTO.class);
        JournalLineDTO journalLineDTO1 = new JournalLineDTO();
        journalLineDTO1.setId(1L);
        JournalLineDTO journalLineDTO2 = new JournalLineDTO();
        assertThat(journalLineDTO1).isNotEqualTo(journalLineDTO2);
        journalLineDTO2.setId(journalLineDTO1.getId());
        assertThat(journalLineDTO1).isEqualTo(journalLineDTO2);
        journalLineDTO2.setId(2L);
        assertThat(journalLineDTO1).isNotEqualTo(journalLineDTO2);
        journalLineDTO1.setId(null);
        assertThat(journalLineDTO1).isNotEqualTo(journalLineDTO2);
    }
}
