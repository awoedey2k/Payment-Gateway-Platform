package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.AmlCheckAsserts.*;
import static io.paymentgateway.core.domain.AmlCheckTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AmlCheckMapperTest {

    private AmlCheckMapper amlCheckMapper;

    @BeforeEach
    void setUp() {
        amlCheckMapper = new AmlCheckMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAmlCheckSample1();
        var actual = amlCheckMapper.toEntity(amlCheckMapper.toDto(expected));
        assertAmlCheckAllPropertiesEquals(expected, actual);
    }
}
