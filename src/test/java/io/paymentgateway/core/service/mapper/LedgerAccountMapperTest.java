package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.LedgerAccountAsserts.*;
import static io.paymentgateway.core.domain.LedgerAccountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerAccountMapperTest {

    private LedgerAccountMapper ledgerAccountMapper;

    @BeforeEach
    void setUp() {
        ledgerAccountMapper = new LedgerAccountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLedgerAccountSample1();
        var actual = ledgerAccountMapper.toEntity(ledgerAccountMapper.toDto(expected));
        assertLedgerAccountAllPropertiesEquals(expected, actual);
    }
}
