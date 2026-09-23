package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.ForexRateAsserts.*;
import static io.paymentgateway.core.domain.ForexRateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForexRateMapperTest {

    private ForexRateMapper forexRateMapper;

    @BeforeEach
    void setUp() {
        forexRateMapper = new ForexRateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getForexRateSample1();
        var actual = forexRateMapper.toEntity(forexRateMapper.toDto(expected));
        assertForexRateAllPropertiesEquals(expected, actual);
    }
}
