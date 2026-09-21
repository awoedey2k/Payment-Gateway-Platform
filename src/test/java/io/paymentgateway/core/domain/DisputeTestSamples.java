package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DisputeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Dispute getDisputeSample1() {
        return new Dispute()
            .id(1L)
            .caseReference("caseReference1")
            .currencyCode("currencyCode1")
            .reasonCode("reasonCode1")
            .reasonDescription("reasonDescription1");
    }

    public static Dispute getDisputeSample2() {
        return new Dispute()
            .id(2L)
            .caseReference("caseReference2")
            .currencyCode("currencyCode2")
            .reasonCode("reasonCode2")
            .reasonDescription("reasonDescription2");
    }

    public static Dispute getDisputeRandomSampleGenerator() {
        return new Dispute()
            .id(longCount.incrementAndGet())
            .caseReference(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString())
            .reasonCode(UUID.randomUUID().toString())
            .reasonDescription(UUID.randomUUID().toString());
    }
}
