package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TenantDirectorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TenantDirector getTenantDirectorSample1() {
        return new TenantDirector().id(1L).fullName("fullName1").identificationNumber("identificationNumber1");
    }

    public static TenantDirector getTenantDirectorSample2() {
        return new TenantDirector().id(2L).fullName("fullName2").identificationNumber("identificationNumber2");
    }

    public static TenantDirector getTenantDirectorRandomSampleGenerator() {
        return new TenantDirector()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .identificationNumber(UUID.randomUUID().toString());
    }
}
