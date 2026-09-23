package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.CountryPaymentMethodAsserts.*;
import static io.paymentgateway.core.domain.CountryPaymentMethodTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CountryPaymentMethodMapperTest {

    private CountryPaymentMethodMapper countryPaymentMethodMapper;

    @BeforeEach
    void setUp() {
        countryPaymentMethodMapper = new CountryPaymentMethodMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCountryPaymentMethodSample1();
        var actual = countryPaymentMethodMapper.toEntity(countryPaymentMethodMapper.toDto(expected));
        assertCountryPaymentMethodAllPropertiesEquals(expected, actual);
    }
}
