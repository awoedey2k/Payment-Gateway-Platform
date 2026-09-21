package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.ApiKeyAsserts.*;
import static io.paymentgateway.core.domain.ApiKeyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiKeyMapperTest {

    private ApiKeyMapper apiKeyMapper;

    @BeforeEach
    void setUp() {
        apiKeyMapper = new ApiKeyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApiKeySample1();
        var actual = apiKeyMapper.toEntity(apiKeyMapper.toDto(expected));
        assertApiKeyAllPropertiesEquals(expected, actual);
    }
}
