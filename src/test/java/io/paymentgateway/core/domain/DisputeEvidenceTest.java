package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.DisputeEvidenceTestSamples.*;
import static io.paymentgateway.core.domain.DisputeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisputeEvidenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DisputeEvidence.class);
        DisputeEvidence disputeEvidence1 = getDisputeEvidenceSample1();
        DisputeEvidence disputeEvidence2 = new DisputeEvidence();
        assertThat(disputeEvidence1).isNotEqualTo(disputeEvidence2);

        disputeEvidence2.setId(disputeEvidence1.getId());
        assertThat(disputeEvidence1).isEqualTo(disputeEvidence2);

        disputeEvidence2 = getDisputeEvidenceSample2();
        assertThat(disputeEvidence1).isNotEqualTo(disputeEvidence2);
    }

    @Test
    void disputeTest() {
        DisputeEvidence disputeEvidence = getDisputeEvidenceRandomSampleGenerator();
        Dispute disputeBack = getDisputeRandomSampleGenerator();

        disputeEvidence.setDispute(disputeBack);
        assertThat(disputeEvidence.getDispute()).isEqualTo(disputeBack);

        disputeEvidence.dispute(null);
        assertThat(disputeEvidence.getDispute()).isNull();
    }
}
