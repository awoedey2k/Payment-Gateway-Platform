package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.JournalEntryAsserts.*;
import static io.paymentgateway.core.domain.JournalEntryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalEntryMapperTest {

    private JournalEntryMapper journalEntryMapper;

    @BeforeEach
    void setUp() {
        journalEntryMapper = new JournalEntryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getJournalEntrySample1();
        var actual = journalEntryMapper.toEntity(journalEntryMapper.toDto(expected));
        assertJournalEntryAllPropertiesEquals(expected, actual);
    }
}
