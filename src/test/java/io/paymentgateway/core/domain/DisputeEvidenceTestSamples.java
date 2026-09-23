package io.paymentgateway.core.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DisputeEvidenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DisputeEvidence getDisputeEvidenceSample1() {
        return new DisputeEvidence()
            .id(1L)
            .fileName("fileName1")
            .fileUrl("fileUrl1")
            .fileSizeBytes(1L)
            .mimeType("mimeType1")
            .sha256Checksum("sha256Checksum1");
    }

    public static DisputeEvidence getDisputeEvidenceSample2() {
        return new DisputeEvidence()
            .id(2L)
            .fileName("fileName2")
            .fileUrl("fileUrl2")
            .fileSizeBytes(2L)
            .mimeType("mimeType2")
            .sha256Checksum("sha256Checksum2");
    }

    public static DisputeEvidence getDisputeEvidenceRandomSampleGenerator() {
        return new DisputeEvidence()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .fileUrl(UUID.randomUUID().toString())
            .fileSizeBytes(longCount.incrementAndGet())
            .mimeType(UUID.randomUUID().toString())
            .sha256Checksum(UUID.randomUUID().toString());
    }
}
