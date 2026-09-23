package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApiKeyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApiKeyDTO.class);
        ApiKeyDTO apiKeyDTO1 = new ApiKeyDTO();
        apiKeyDTO1.setId(1L);
        ApiKeyDTO apiKeyDTO2 = new ApiKeyDTO();
        assertThat(apiKeyDTO1).isNotEqualTo(apiKeyDTO2);
        apiKeyDTO2.setId(apiKeyDTO1.getId());
        assertThat(apiKeyDTO1).isEqualTo(apiKeyDTO2);
        apiKeyDTO2.setId(2L);
        assertThat(apiKeyDTO1).isNotEqualTo(apiKeyDTO2);
        apiKeyDTO1.setId(null);
        assertThat(apiKeyDTO1).isNotEqualTo(apiKeyDTO2);
    }
}
