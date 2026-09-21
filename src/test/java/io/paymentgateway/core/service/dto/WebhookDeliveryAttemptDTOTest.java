package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookDeliveryAttemptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookDeliveryAttemptDTO.class);
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO1 = new WebhookDeliveryAttemptDTO();
        webhookDeliveryAttemptDTO1.setId(1L);
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO2 = new WebhookDeliveryAttemptDTO();
        assertThat(webhookDeliveryAttemptDTO1).isNotEqualTo(webhookDeliveryAttemptDTO2);
        webhookDeliveryAttemptDTO2.setId(webhookDeliveryAttemptDTO1.getId());
        assertThat(webhookDeliveryAttemptDTO1).isEqualTo(webhookDeliveryAttemptDTO2);
        webhookDeliveryAttemptDTO2.setId(2L);
        assertThat(webhookDeliveryAttemptDTO1).isNotEqualTo(webhookDeliveryAttemptDTO2);
        webhookDeliveryAttemptDTO1.setId(null);
        assertThat(webhookDeliveryAttemptDTO1).isNotEqualTo(webhookDeliveryAttemptDTO2);
    }
}
