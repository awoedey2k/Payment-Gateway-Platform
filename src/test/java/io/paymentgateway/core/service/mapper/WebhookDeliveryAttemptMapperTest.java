package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.WebhookDeliveryAttemptAsserts.*;
import static io.paymentgateway.core.domain.WebhookDeliveryAttemptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhookDeliveryAttemptMapperTest {

    private WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper;

    @BeforeEach
    void setUp() {
        webhookDeliveryAttemptMapper = new WebhookDeliveryAttemptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWebhookDeliveryAttemptSample1();
        var actual = webhookDeliveryAttemptMapper.toEntity(webhookDeliveryAttemptMapper.toDto(expected));
        assertWebhookDeliveryAttemptAllPropertiesEquals(expected, actual);
    }
}
