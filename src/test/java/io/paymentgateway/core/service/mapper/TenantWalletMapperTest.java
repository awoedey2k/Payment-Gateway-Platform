package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.TenantWalletAsserts.*;
import static io.paymentgateway.core.domain.TenantWalletTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantWalletMapperTest {

    private TenantWalletMapper tenantWalletMapper;

    @BeforeEach
    void setUp() {
        tenantWalletMapper = new TenantWalletMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTenantWalletSample1();
        var actual = tenantWalletMapper.toEntity(tenantWalletMapper.toDto(expected));
        assertTenantWalletAllPropertiesEquals(expected, actual);
    }
}
