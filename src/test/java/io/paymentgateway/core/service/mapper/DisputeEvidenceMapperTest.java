package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.DisputeEvidenceAsserts.*;
import static io.paymentgateway.core.domain.DisputeEvidenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisputeEvidenceMapperTest {

    private DisputeEvidenceMapper disputeEvidenceMapper;

    @BeforeEach
    void setUp() {
        disputeEvidenceMapper = new DisputeEvidenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDisputeEvidenceSample1();
        var actual = disputeEvidenceMapper.toEntity(disputeEvidenceMapper.toDto(expected));
        assertDisputeEvidenceAllPropertiesEquals(expected, actual);
    }
}
