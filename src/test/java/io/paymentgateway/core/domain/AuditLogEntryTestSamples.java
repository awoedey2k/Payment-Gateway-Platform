package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogEntryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditLogEntry getAuditLogEntrySample1() {
        return new AuditLogEntry()
            .id(1L)
            .actorId("actorId1")
            .action("action1")
            .entityType("entityType1")
            .entityId("entityId1")
            .previousHash("previousHash1")
            .entryHash("entryHash1");
    }

    public static AuditLogEntry getAuditLogEntrySample2() {
        return new AuditLogEntry()
            .id(2L)
            .actorId("actorId2")
            .action("action2")
            .entityType("entityType2")
            .entityId("entityId2")
            .previousHash("previousHash2")
            .entryHash("entryHash2");
    }

    public static AuditLogEntry getAuditLogEntryRandomSampleGenerator() {
        return new AuditLogEntry()
            .id(longCount.incrementAndGet())
            .actorId(UUID.randomUUID().toString())
            .action(UUID.randomUUID().toString())
            .entityType(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .previousHash(UUID.randomUUID().toString())
            .entryHash(UUID.randomUUID().toString());
    }
}
