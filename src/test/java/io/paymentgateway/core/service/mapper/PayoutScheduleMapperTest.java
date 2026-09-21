package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.PayoutScheduleAsserts.*;
import static io.paymentgateway.core.domain.PayoutScheduleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayoutScheduleMapperTest {

    private PayoutScheduleMapper payoutScheduleMapper;

    @BeforeEach
    void setUp() {
        payoutScheduleMapper = new PayoutScheduleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPayoutScheduleSample1();
        var actual = payoutScheduleMapper.toEntity(payoutScheduleMapper.toDto(expected));
        assertPayoutScheduleAllPropertiesEquals(expected, actual);
    }
}
