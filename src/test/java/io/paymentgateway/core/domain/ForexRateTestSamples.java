package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ForexRateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ForexRate getForexRateSample1() {
        return new ForexRate().id(1L).baseCurrency("baseCurrency1").quoteCurrency("quoteCurrency1").platformSpreadBps(1);
    }

    public static ForexRate getForexRateSample2() {
        return new ForexRate().id(2L).baseCurrency("baseCurrency2").quoteCurrency("quoteCurrency2").platformSpreadBps(2);
    }

    public static ForexRate getForexRateRandomSampleGenerator() {
        return new ForexRate()
            .id(longCount.incrementAndGet())
            .baseCurrency(UUID.randomUUID().toString())
            .quoteCurrency(UUID.randomUUID().toString())
            .platformSpreadBps(intCount.incrementAndGet());
    }
}
