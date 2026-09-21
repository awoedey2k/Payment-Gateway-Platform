package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmlCheckDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmlCheckDTO.class);
        AmlCheckDTO amlCheckDTO1 = new AmlCheckDTO();
        amlCheckDTO1.setId(1L);
        AmlCheckDTO amlCheckDTO2 = new AmlCheckDTO();
        assertThat(amlCheckDTO1).isNotEqualTo(amlCheckDTO2);
        amlCheckDTO2.setId(amlCheckDTO1.getId());
        assertThat(amlCheckDTO1).isEqualTo(amlCheckDTO2);
        amlCheckDTO2.setId(2L);
        assertThat(amlCheckDTO1).isNotEqualTo(amlCheckDTO2);
        amlCheckDTO1.setId(null);
        assertThat(amlCheckDTO1).isNotEqualTo(amlCheckDTO2);
    }
}
