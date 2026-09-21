package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ForexRateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ForexRateDTO.class);
        ForexRateDTO forexRateDTO1 = new ForexRateDTO();
        forexRateDTO1.setId(1L);
        ForexRateDTO forexRateDTO2 = new ForexRateDTO();
        assertThat(forexRateDTO1).isNotEqualTo(forexRateDTO2);
        forexRateDTO2.setId(forexRateDTO1.getId());
        assertThat(forexRateDTO1).isEqualTo(forexRateDTO2);
        forexRateDTO2.setId(2L);
        assertThat(forexRateDTO1).isNotEqualTo(forexRateDTO2);
        forexRateDTO1.setId(null);
        assertThat(forexRateDTO1).isNotEqualTo(forexRateDTO2);
    }
}
