package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.TenantDirectorAsserts.*;
import static io.paymentgateway.core.domain.TenantDirectorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantDirectorMapperTest {

    private TenantDirectorMapper tenantDirectorMapper;

    @BeforeEach
    void setUp() {
        tenantDirectorMapper = new TenantDirectorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTenantDirectorSample1();
        var actual = tenantDirectorMapper.toEntity(tenantDirectorMapper.toDto(expected));
        assertTenantDirectorAllPropertiesEquals(expected, actual);
    }
}
