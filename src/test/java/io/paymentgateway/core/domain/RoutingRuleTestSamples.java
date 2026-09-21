package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoutingRuleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static RoutingRule getRoutingRuleSample1() {
        return new RoutingRule()
            .id(1L)
            .priority(1)
            .countryCode("countryCode1")
            .currencyCode("currencyCode1")
            .cardBrand("cardBrand1")
            .primaryAdapter("primaryAdapter1")
            .fallbackAdapter("fallbackAdapter1")
            .maxRetries(1);
    }

    public static RoutingRule getRoutingRuleSample2() {
        return new RoutingRule()
            .id(2L)
            .priority(2)
            .countryCode("countryCode2")
            .currencyCode("currencyCode2")
            .cardBrand("cardBrand2")
            .primaryAdapter("primaryAdapter2")
            .fallbackAdapter("fallbackAdapter2")
            .maxRetries(2);
    }

    public static RoutingRule getRoutingRuleRandomSampleGenerator() {
        return new RoutingRule()
            .id(longCount.incrementAndGet())
            .priority(intCount.incrementAndGet())
            .countryCode(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString())
            .cardBrand(UUID.randomUUID().toString())
            .primaryAdapter(UUID.randomUUID().toString())
            .fallbackAdapter(UUID.randomUUID().toString())
            .maxRetries(intCount.incrementAndGet());
    }
}
