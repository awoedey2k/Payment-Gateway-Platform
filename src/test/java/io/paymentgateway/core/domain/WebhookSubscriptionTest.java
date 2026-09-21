package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.WebhookSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookSubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookSubscription.class);
        WebhookSubscription webhookSubscription1 = getWebhookSubscriptionSample1();
        WebhookSubscription webhookSubscription2 = new WebhookSubscription();
        assertThat(webhookSubscription1).isNotEqualTo(webhookSubscription2);

        webhookSubscription2.setId(webhookSubscription1.getId());
        assertThat(webhookSubscription1).isEqualTo(webhookSubscription2);

        webhookSubscription2 = getWebhookSubscriptionSample2();
        assertThat(webhookSubscription1).isNotEqualTo(webhookSubscription2);
    }

    @Test
    void tenantTest() {
        WebhookSubscription webhookSubscription = getWebhookSubscriptionRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        webhookSubscription.setTenant(corporateTenantBack);
        assertThat(webhookSubscription.getTenant()).isEqualTo(corporateTenantBack);

        webhookSubscription.tenant(null);
        assertThat(webhookSubscription.getTenant()).isNull();
    }
}
