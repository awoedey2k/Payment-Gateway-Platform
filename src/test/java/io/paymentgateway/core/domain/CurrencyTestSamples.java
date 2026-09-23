package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CurrencyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Currency getCurrencySample1() {
        return new Currency().id(1L).code("code1").name("name1").minorUnit(1);
    }

    public static Currency getCurrencySample2() {
        return new Currency().id(2L).code("code2").name("name2").minorUnit(2);
    }

    public static Currency getCurrencyRandomSampleGenerator() {
        return new Currency()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .minorUnit(intCount.incrementAndGet());
    }
}
