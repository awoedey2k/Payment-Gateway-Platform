package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisputeEvidenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DisputeEvidenceDTO.class);
        DisputeEvidenceDTO disputeEvidenceDTO1 = new DisputeEvidenceDTO();
        disputeEvidenceDTO1.setId(1L);
        DisputeEvidenceDTO disputeEvidenceDTO2 = new DisputeEvidenceDTO();
        assertThat(disputeEvidenceDTO1).isNotEqualTo(disputeEvidenceDTO2);
        disputeEvidenceDTO2.setId(disputeEvidenceDTO1.getId());
        assertThat(disputeEvidenceDTO1).isEqualTo(disputeEvidenceDTO2);
        disputeEvidenceDTO2.setId(2L);
        assertThat(disputeEvidenceDTO1).isNotEqualTo(disputeEvidenceDTO2);
        disputeEvidenceDTO1.setId(null);
        assertThat(disputeEvidenceDTO1).isNotEqualTo(disputeEvidenceDTO2);
    }
}
