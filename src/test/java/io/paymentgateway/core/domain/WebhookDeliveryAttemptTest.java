package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.WebhookDeliveryAttemptTestSamples.*;
import static io.paymentgateway.core.domain.WebhookSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookDeliveryAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookDeliveryAttempt.class);
        WebhookDeliveryAttempt webhookDeliveryAttempt1 = getWebhookDeliveryAttemptSample1();
        WebhookDeliveryAttempt webhookDeliveryAttempt2 = new WebhookDeliveryAttempt();
        assertThat(webhookDeliveryAttempt1).isNotEqualTo(webhookDeliveryAttempt2);

        webhookDeliveryAttempt2.setId(webhookDeliveryAttempt1.getId());
        assertThat(webhookDeliveryAttempt1).isEqualTo(webhookDeliveryAttempt2);

        webhookDeliveryAttempt2 = getWebhookDeliveryAttemptSample2();
        assertThat(webhookDeliveryAttempt1).isNotEqualTo(webhookDeliveryAttempt2);
    }

    @Test
    void subscriptionTest() {
        WebhookDeliveryAttempt webhookDeliveryAttempt = getWebhookDeliveryAttemptRandomSampleGenerator();
        WebhookSubscription webhookSubscriptionBack = getWebhookSubscriptionRandomSampleGenerator();

        webhookDeliveryAttempt.setSubscription(webhookSubscriptionBack);
        assertThat(webhookDeliveryAttempt.getSubscription()).isEqualTo(webhookSubscriptionBack);

        webhookDeliveryAttempt.subscription(null);
        assertThat(webhookDeliveryAttempt.getSubscription()).isNull();
    }
}
