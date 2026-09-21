package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Transaction getTransactionSample1() {
        return new Transaction()
            .id(1L)
            .reference("reference1")
            .tenantReference("tenantReference1")
            .currencyCode("currencyCode1")
            .countryCode("countryCode1")
            .paymentMethodCode("paymentMethodCode1")
            .idempotencyKey("idempotencyKey1")
            .customerEmail("customerEmail1")
            .customerPhone("customerPhone1");
    }

    public static Transaction getTransactionSample2() {
        return new Transaction()
            .id(2L)
            .reference("reference2")
            .tenantReference("tenantReference2")
            .currencyCode("currencyCode2")
            .countryCode("countryCode2")
            .paymentMethodCode("paymentMethodCode2")
            .idempotencyKey("idempotencyKey2")
            .customerEmail("customerEmail2")
            .customerPhone("customerPhone2");
    }

    public static Transaction getTransactionRandomSampleGenerator() {
        return new Transaction()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .tenantReference(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString())
            .paymentMethodCode(UUID.randomUUID().toString())
            .idempotencyKey(UUID.randomUUID().toString())
            .customerEmail(UUID.randomUUID().toString())
            .customerPhone(UUID.randomUUID().toString());
    }
}
