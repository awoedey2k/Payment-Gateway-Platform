package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentMethodTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PaymentMethod getPaymentMethodSample1() {
        return new PaymentMethod().id(1L).code("code1").displayName("displayName1");
    }

    public static PaymentMethod getPaymentMethodSample2() {
        return new PaymentMethod().id(2L).code("code2").displayName("displayName2");
    }

    public static PaymentMethod getPaymentMethodRandomSampleGenerator() {
        return new PaymentMethod()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .displayName(UUID.randomUUID().toString());
    }
}
