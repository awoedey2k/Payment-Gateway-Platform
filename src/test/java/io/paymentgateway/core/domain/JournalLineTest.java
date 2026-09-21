package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.JournalEntryTestSamples.*;
import static io.paymentgateway.core.domain.JournalLineTestSamples.*;
import static io.paymentgateway.core.domain.LedgerAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalLine.class);
        JournalLine journalLine1 = getJournalLineSample1();
        JournalLine journalLine2 = new JournalLine();
        assertThat(journalLine1).isNotEqualTo(journalLine2);

        journalLine2.setId(journalLine1.getId());
        assertThat(journalLine1).isEqualTo(journalLine2);

        journalLine2 = getJournalLineSample2();
        assertThat(journalLine1).isNotEqualTo(journalLine2);
    }

    @Test
    void accountTest() {
        JournalLine journalLine = getJournalLineRandomSampleGenerator();
        LedgerAccount ledgerAccountBack = getLedgerAccountRandomSampleGenerator();

        journalLine.setAccount(ledgerAccountBack);
        assertThat(journalLine.getAccount()).isEqualTo(ledgerAccountBack);

        journalLine.account(null);
        assertThat(journalLine.getAccount()).isNull();
    }

    @Test
    void journalEntryTest() {
        JournalLine journalLine = getJournalLineRandomSampleGenerator();
        JournalEntry journalEntryBack = getJournalEntryRandomSampleGenerator();

        journalLine.setJournalEntry(journalEntryBack);
        assertThat(journalLine.getJournalEntry()).isEqualTo(journalEntryBack);

        journalLine.journalEntry(null);
        assertThat(journalLine.getJournalEntry()).isNull();
    }
}
