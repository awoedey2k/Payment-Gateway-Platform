package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LedgerAccountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static LedgerAccount getLedgerAccountSample1() {
        return new LedgerAccount().id(1L).accountCode("accountCode1").currencyCode("currencyCode1");
    }

    public static LedgerAccount getLedgerAccountSample2() {
        return new LedgerAccount().id(2L).accountCode("accountCode2").currencyCode("currencyCode2");
    }

    public static LedgerAccount getLedgerAccountRandomSampleGenerator() {
        return new LedgerAccount()
            .id(longCount.incrementAndGet())
            .accountCode(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString());
    }
}
