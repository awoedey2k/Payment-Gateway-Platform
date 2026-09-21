package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PayoutScheduleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayoutScheduleDTO.class);
        PayoutScheduleDTO payoutScheduleDTO1 = new PayoutScheduleDTO();
        payoutScheduleDTO1.setId(1L);
        PayoutScheduleDTO payoutScheduleDTO2 = new PayoutScheduleDTO();
        assertThat(payoutScheduleDTO1).isNotEqualTo(payoutScheduleDTO2);
        payoutScheduleDTO2.setId(payoutScheduleDTO1.getId());
        assertThat(payoutScheduleDTO1).isEqualTo(payoutScheduleDTO2);
        payoutScheduleDTO2.setId(2L);
        assertThat(payoutScheduleDTO1).isNotEqualTo(payoutScheduleDTO2);
        payoutScheduleDTO1.setId(null);
        assertThat(payoutScheduleDTO1).isNotEqualTo(payoutScheduleDTO2);
    }
}
