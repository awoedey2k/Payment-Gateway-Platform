package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TenantFeeConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TenantFeeConfig getTenantFeeConfigSample1() {
        return new TenantFeeConfig().id(1L);
    }

    public static TenantFeeConfig getTenantFeeConfigSample2() {
        return new TenantFeeConfig().id(2L);
    }

    public static TenantFeeConfig getTenantFeeConfigRandomSampleGenerator() {
        return new TenantFeeConfig().id(longCount.incrementAndGet());
    }
}
