package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JournalEntryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static JournalEntry getJournalEntrySample1() {
        return new JournalEntry().id(1L).reference("reference1").description("description1");
    }

    public static JournalEntry getJournalEntrySample2() {
        return new JournalEntry().id(2L).reference("reference2").description("description2");
    }

    public static JournalEntry getJournalEntryRandomSampleGenerator() {
        return new JournalEntry()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
