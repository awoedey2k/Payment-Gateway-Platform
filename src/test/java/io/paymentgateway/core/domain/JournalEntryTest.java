package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.JournalEntryTestSamples.*;
import static io.paymentgateway.core.domain.JournalLineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JournalEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalEntry.class);
        JournalEntry journalEntry1 = getJournalEntrySample1();
        JournalEntry journalEntry2 = new JournalEntry();
        assertThat(journalEntry1).isNotEqualTo(journalEntry2);

        journalEntry2.setId(journalEntry1.getId());
        assertThat(journalEntry1).isEqualTo(journalEntry2);

        journalEntry2 = getJournalEntrySample2();
        assertThat(journalEntry1).isNotEqualTo(journalEntry2);
    }

    @Test
    void journalLineTest() {
        JournalEntry journalEntry = getJournalEntryRandomSampleGenerator();
        JournalLine journalLineBack = getJournalLineRandomSampleGenerator();

        journalEntry.addJournalLine(journalLineBack);
        assertThat(journalEntry.getJournalLines()).containsOnly(journalLineBack);
        assertThat(journalLineBack.getJournalEntry()).isEqualTo(journalEntry);

        journalEntry.removeJournalLine(journalLineBack);
        assertThat(journalEntry.getJournalLines()).doesNotContain(journalLineBack);
        assertThat(journalLineBack.getJournalEntry()).isNull();

        journalEntry.journalLines(new HashSet<>(Set.of(journalLineBack)));
        assertThat(journalEntry.getJournalLines()).containsOnly(journalLineBack);
        assertThat(journalLineBack.getJournalEntry()).isEqualTo(journalEntry);

        journalEntry.setJournalLines(new HashSet<>());
        assertThat(journalEntry.getJournalLines()).doesNotContain(journalLineBack);
        assertThat(journalLineBack.getJournalEntry()).isNull();
    }
}
