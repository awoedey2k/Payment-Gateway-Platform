package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.TenantFeeConfigAsserts.*;
import static io.paymentgateway.core.domain.TenantFeeConfigTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantFeeConfigMapperTest {

    private TenantFeeConfigMapper tenantFeeConfigMapper;

    @BeforeEach
    void setUp() {
        tenantFeeConfigMapper = new TenantFeeConfigMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTenantFeeConfigSample1();
        var actual = tenantFeeConfigMapper.toEntity(tenantFeeConfigMapper.toDto(expected));
        assertTenantFeeConfigAllPropertiesEquals(expected, actual);
    }
}
