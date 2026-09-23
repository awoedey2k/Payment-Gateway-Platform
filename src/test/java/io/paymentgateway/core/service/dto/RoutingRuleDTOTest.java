package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoutingRuleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoutingRuleDTO.class);
        RoutingRuleDTO routingRuleDTO1 = new RoutingRuleDTO();
        routingRuleDTO1.setId(1L);
        RoutingRuleDTO routingRuleDTO2 = new RoutingRuleDTO();
        assertThat(routingRuleDTO1).isNotEqualTo(routingRuleDTO2);
        routingRuleDTO2.setId(routingRuleDTO1.getId());
        assertThat(routingRuleDTO1).isEqualTo(routingRuleDTO2);
        routingRuleDTO2.setId(2L);
        assertThat(routingRuleDTO1).isNotEqualTo(routingRuleDTO2);
        routingRuleDTO1.setId(null);
        assertThat(routingRuleDTO1).isNotEqualTo(routingRuleDTO2);
    }
}
