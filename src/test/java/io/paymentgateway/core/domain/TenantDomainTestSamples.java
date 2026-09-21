package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TenantDomainTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TenantDomain getTenantDomainSample1() {
        return new TenantDomain().id(1L).customDomain("customDomain1").supportedLocales("supportedLocales1");
    }

    public static TenantDomain getTenantDomainSample2() {
        return new TenantDomain().id(2L).customDomain("customDomain2").supportedLocales("supportedLocales2");
    }

    public static TenantDomain getTenantDomainRandomSampleGenerator() {
        return new TenantDomain()
            .id(longCount.incrementAndGet())
            .customDomain(UUID.randomUUID().toString())
            .supportedLocales(UUID.randomUUID().toString());
    }
}
