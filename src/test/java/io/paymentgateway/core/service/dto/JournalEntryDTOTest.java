package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalEntryDTO.class);
        JournalEntryDTO journalEntryDTO1 = new JournalEntryDTO();
        journalEntryDTO1.setId(1L);
        JournalEntryDTO journalEntryDTO2 = new JournalEntryDTO();
        assertThat(journalEntryDTO1).isNotEqualTo(journalEntryDTO2);
        journalEntryDTO2.setId(journalEntryDTO1.getId());
        assertThat(journalEntryDTO1).isEqualTo(journalEntryDTO2);
        journalEntryDTO2.setId(2L);
        assertThat(journalEntryDTO1).isNotEqualTo(journalEntryDTO2);
        journalEntryDTO1.setId(null);
        assertThat(journalEntryDTO1).isNotEqualTo(journalEntryDTO2);
    }
}
