package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.TenantDomainAsserts.*;
import static io.paymentgateway.core.domain.TenantDomainTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantDomainMapperTest {

    private TenantDomainMapper tenantDomainMapper;

    @BeforeEach
    void setUp() {
        tenantDomainMapper = new TenantDomainMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTenantDomainSample1();
        var actual = tenantDomainMapper.toEntity(tenantDomainMapper.toDto(expected));
        assertTenantDomainAllPropertiesEquals(expected, actual);
    }
}
