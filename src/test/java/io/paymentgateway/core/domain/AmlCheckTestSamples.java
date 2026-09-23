package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AmlCheckTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static AmlCheck getAmlCheckSample1() {
        return new AmlCheck().id(1L).riskScore(1).ruleTriggered("ruleTriggered1");
    }

    public static AmlCheck getAmlCheckSample2() {
        return new AmlCheck().id(2L).riskScore(2).ruleTriggered("ruleTriggered2");
    }

    public static AmlCheck getAmlCheckRandomSampleGenerator() {
        return new AmlCheck()
            .id(longCount.incrementAndGet())
            .riskScore(intCount.incrementAndGet())
            .ruleTriggered(UUID.randomUUID().toString());
    }
}
