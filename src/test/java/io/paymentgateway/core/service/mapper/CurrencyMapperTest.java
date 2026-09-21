package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.CurrencyAsserts.*;
import static io.paymentgateway.core.domain.CurrencyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurrencyMapperTest {

    private CurrencyMapper currencyMapper;

    @BeforeEach
    void setUp() {
        currencyMapper = new CurrencyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCurrencySample1();
        var actual = currencyMapper.toEntity(currencyMapper.toDto(expected));
        assertCurrencyAllPropertiesEquals(expected, actual);
    }
}
