package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RefundTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Refund getRefundSample1() {
        return new Refund().id(1L).reference("reference1");
    }

    public static Refund getRefundSample2() {
        return new Refund().id(2L).reference("reference2");
    }

    public static Refund getRefundRandomSampleGenerator() {
        return new Refund().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString());
    }
}
