package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WebhookDeliveryAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static WebhookDeliveryAttempt getWebhookDeliveryAttemptSample1() {
        return new WebhookDeliveryAttempt().id(1L).eventType("eventType1").httpStatusCode(1).attemptNumber(1);
    }

    public static WebhookDeliveryAttempt getWebhookDeliveryAttemptSample2() {
        return new WebhookDeliveryAttempt().id(2L).eventType("eventType2").httpStatusCode(2).attemptNumber(2);
    }

    public static WebhookDeliveryAttempt getWebhookDeliveryAttemptRandomSampleGenerator() {
        return new WebhookDeliveryAttempt()
            .id(longCount.incrementAndGet())
            .eventType(UUID.randomUUID().toString())
            .httpStatusCode(intCount.incrementAndGet())
            .attemptNumber(intCount.incrementAndGet());
    }
}
