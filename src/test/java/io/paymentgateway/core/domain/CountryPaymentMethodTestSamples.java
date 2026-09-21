package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CountryPaymentMethodTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CountryPaymentMethod getCountryPaymentMethodSample1() {
        return new CountryPaymentMethod().id(1L);
    }

    public static CountryPaymentMethod getCountryPaymentMethodSample2() {
        return new CountryPaymentMethod().id(2L);
    }

    public static CountryPaymentMethod getCountryPaymentMethodRandomSampleGenerator() {
        return new CountryPaymentMethod().id(longCount.incrementAndGet());
    }
}
