package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.DisputeAsserts.*;
import static io.paymentgateway.core.domain.DisputeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisputeMapperTest {

    private DisputeMapper disputeMapper;

    @BeforeEach
    void setUp() {
        disputeMapper = new DisputeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDisputeSample1();
        var actual = disputeMapper.toEntity(disputeMapper.toDto(expected));
        assertDisputeAllPropertiesEquals(expected, actual);
    }
}
