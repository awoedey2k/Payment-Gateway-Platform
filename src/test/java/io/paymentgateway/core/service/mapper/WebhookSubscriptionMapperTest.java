package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.WebhookSubscriptionAsserts.*;
import static io.paymentgateway.core.domain.WebhookSubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhookSubscriptionMapperTest {

    private WebhookSubscriptionMapper webhookSubscriptionMapper;

    @BeforeEach
    void setUp() {
        webhookSubscriptionMapper = new WebhookSubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWebhookSubscriptionSample1();
        var actual = webhookSubscriptionMapper.toEntity(webhookSubscriptionMapper.toDto(expected));
        assertWebhookSubscriptionAllPropertiesEquals(expected, actual);
    }
}
