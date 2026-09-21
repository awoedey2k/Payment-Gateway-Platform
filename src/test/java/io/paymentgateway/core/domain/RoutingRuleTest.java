package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.RoutingRuleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoutingRuleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoutingRule.class);
        RoutingRule routingRule1 = getRoutingRuleSample1();
        RoutingRule routingRule2 = new RoutingRule();
        assertThat(routingRule1).isNotEqualTo(routingRule2);

        routingRule2.setId(routingRule1.getId());
        assertThat(routingRule1).isEqualTo(routingRule2);

        routingRule2 = getRoutingRuleSample2();
        assertThat(routingRule1).isNotEqualTo(routingRule2);
    }

    @Test
    void tenantTest() {
        RoutingRule routingRule = getRoutingRuleRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        routingRule.setTenant(corporateTenantBack);
        assertThat(routingRule.getTenant()).isEqualTo(corporateTenantBack);

        routingRule.tenant(null);
        assertThat(routingRule.getTenant()).isNull();
    }
}
