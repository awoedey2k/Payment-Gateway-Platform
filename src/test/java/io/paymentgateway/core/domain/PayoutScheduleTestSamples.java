package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PayoutScheduleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PayoutSchedule getPayoutScheduleSample1() {
        return new PayoutSchedule().id(1L);
    }

    public static PayoutSchedule getPayoutScheduleSample2() {
        return new PayoutSchedule().id(2L);
    }

    public static PayoutSchedule getPayoutScheduleRandomSampleGenerator() {
        return new PayoutSchedule().id(longCount.incrementAndGet());
    }
}
