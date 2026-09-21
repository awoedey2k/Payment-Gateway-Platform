package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.SettlementBatchAsserts.*;
import static io.paymentgateway.core.domain.SettlementBatchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettlementBatchMapperTest {

    private SettlementBatchMapper settlementBatchMapper;

    @BeforeEach
    void setUp() {
        settlementBatchMapper = new SettlementBatchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSettlementBatchSample1();
        var actual = settlementBatchMapper.toEntity(settlementBatchMapper.toDto(expected));
        assertSettlementBatchAllPropertiesEquals(expected, actual);
    }
}
