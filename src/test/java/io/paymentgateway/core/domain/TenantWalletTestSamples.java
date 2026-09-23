package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TenantWalletTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TenantWallet getTenantWalletSample1() {
        return new TenantWallet().id(1L).currencyCode("currencyCode1");
    }

    public static TenantWallet getTenantWalletSample2() {
        return new TenantWallet().id(2L).currencyCode("currencyCode2");
    }

    public static TenantWallet getTenantWalletRandomSampleGenerator() {
        return new TenantWallet().id(longCount.incrementAndGet()).currencyCode(UUID.randomUUID().toString());
    }
}
