package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class JournalLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static JournalLine getJournalLineSample1() {
        return new JournalLine().id(1L);
    }

    public static JournalLine getJournalLineSample2() {
        return new JournalLine().id(2L);
    }

    public static JournalLine getJournalLineRandomSampleGenerator() {
        return new JournalLine().id(longCount.incrementAndGet());
    }
}
