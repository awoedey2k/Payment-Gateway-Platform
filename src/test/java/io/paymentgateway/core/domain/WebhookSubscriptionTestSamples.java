package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WebhookSubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WebhookSubscription getWebhookSubscriptionSample1() {
        return new WebhookSubscription().id(1L).targetUrl("targetUrl1").secretHash("secretHash1");
    }

    public static WebhookSubscription getWebhookSubscriptionSample2() {
        return new WebhookSubscription().id(2L).targetUrl("targetUrl2").secretHash("secretHash2");
    }

    public static WebhookSubscription getWebhookSubscriptionRandomSampleGenerator() {
        return new WebhookSubscription()
            .id(longCount.incrementAndGet())
            .targetUrl(UUID.randomUUID().toString())
            .secretHash(UUID.randomUUID().toString());
    }
}
