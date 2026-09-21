package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CorporateTenantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static CorporateTenant getCorporateTenantSample1() {
        return new CorporateTenant()
            .id(1L)
            .legalBusinessName("legalBusinessName1")
            .businessRegistrationNumber("businessRegistrationNumber1")
            .taxIdentificationNumber("taxIdentificationNumber1")
            .riskScore(1);
    }

    public static CorporateTenant getCorporateTenantSample2() {
        return new CorporateTenant()
            .id(2L)
            .legalBusinessName("legalBusinessName2")
            .businessRegistrationNumber("businessRegistrationNumber2")
            .taxIdentificationNumber("taxIdentificationNumber2")
            .riskScore(2);
    }

    public static CorporateTenant getCorporateTenantRandomSampleGenerator() {
        return new CorporateTenant()
            .id(longCount.incrementAndGet())
            .legalBusinessName(UUID.randomUUID().toString())
            .businessRegistrationNumber(UUID.randomUUID().toString())
            .taxIdentificationNumber(UUID.randomUUID().toString())
            .riskScore(intCount.incrementAndGet());
    }
}
