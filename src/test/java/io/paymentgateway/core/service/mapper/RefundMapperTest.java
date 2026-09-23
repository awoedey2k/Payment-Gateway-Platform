package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.RefundAsserts.*;
import static io.paymentgateway.core.domain.RefundTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefundMapperTest {

    private RefundMapper refundMapper;

    @BeforeEach
    void setUp() {
        refundMapper = new RefundMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRefundSample1();
        var actual = refundMapper.toEntity(refundMapper.toDto(expected));
        assertRefundAllPropertiesEquals(expected, actual);
    }
}
