package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.RoutingRuleAsserts.*;
import static io.paymentgateway.core.domain.RoutingRuleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoutingRuleMapperTest {

    private RoutingRuleMapper routingRuleMapper;

    @BeforeEach
    void setUp() {
        routingRuleMapper = new RoutingRuleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRoutingRuleSample1();
        var actual = routingRuleMapper.toEntity(routingRuleMapper.toDto(expected));
        assertRoutingRuleAllPropertiesEquals(expected, actual);
    }
}
