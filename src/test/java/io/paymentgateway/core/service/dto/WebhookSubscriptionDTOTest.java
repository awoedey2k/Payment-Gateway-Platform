package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookSubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookSubscriptionDTO.class);
        WebhookSubscriptionDTO webhookSubscriptionDTO1 = new WebhookSubscriptionDTO();
        webhookSubscriptionDTO1.setId(1L);
        WebhookSubscriptionDTO webhookSubscriptionDTO2 = new WebhookSubscriptionDTO();
        assertThat(webhookSubscriptionDTO1).isNotEqualTo(webhookSubscriptionDTO2);
        webhookSubscriptionDTO2.setId(webhookSubscriptionDTO1.getId());
        assertThat(webhookSubscriptionDTO1).isEqualTo(webhookSubscriptionDTO2);
        webhookSubscriptionDTO2.setId(2L);
        assertThat(webhookSubscriptionDTO1).isNotEqualTo(webhookSubscriptionDTO2);
        webhookSubscriptionDTO1.setId(null);
        assertThat(webhookSubscriptionDTO1).isNotEqualTo(webhookSubscriptionDTO2);
    }
}
