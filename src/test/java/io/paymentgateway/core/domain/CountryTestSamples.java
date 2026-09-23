package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CountryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Country getCountrySample1() {
        return new Country().id(1L).isoCode("isoCode1").name("name1").defaultCurrencyCode("defaultCurrencyCode1");
    }

    public static Country getCountrySample2() {
        return new Country().id(2L).isoCode("isoCode2").name("name2").defaultCurrencyCode("defaultCurrencyCode2");
    }

    public static Country getCountryRandomSampleGenerator() {
        return new Country()
            .id(longCount.incrementAndGet())
            .isoCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .defaultCurrencyCode(UUID.randomUUID().toString());
    }
}
