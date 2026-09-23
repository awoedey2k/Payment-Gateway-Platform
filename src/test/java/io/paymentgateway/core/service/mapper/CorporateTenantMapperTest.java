package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.CorporateTenantAsserts.*;
import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorporateTenantMapperTest {

    private CorporateTenantMapper corporateTenantMapper;

    @BeforeEach
    void setUp() {
        corporateTenantMapper = new CorporateTenantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCorporateTenantSample1();
        var actual = corporateTenantMapper.toEntity(corporateTenantMapper.toDto(expected));
        assertCorporateTenantAllPropertiesEquals(expected, actual);
    }
}
