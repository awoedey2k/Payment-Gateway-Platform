package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.JournalLineAsserts.*;
import static io.paymentgateway.core.domain.JournalLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalLineMapperTest {

    private JournalLineMapper journalLineMapper;

    @BeforeEach
    void setUp() {
        journalLineMapper = new JournalLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getJournalLineSample1();
        var actual = journalLineMapper.toEntity(journalLineMapper.toDto(expected));
        assertJournalLineAllPropertiesEquals(expected, actual);
    }
}
