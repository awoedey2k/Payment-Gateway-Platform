package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.ApiKeyTestSamples.*;
import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApiKeyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApiKey.class);
        ApiKey apiKey1 = getApiKeySample1();
        ApiKey apiKey2 = new ApiKey();
        assertThat(apiKey1).isNotEqualTo(apiKey2);

        apiKey2.setId(apiKey1.getId());
        assertThat(apiKey1).isEqualTo(apiKey2);

        apiKey2 = getApiKeySample2();
        assertThat(apiKey1).isNotEqualTo(apiKey2);
    }

    @Test
    void tenantTest() {
        ApiKey apiKey = getApiKeyRandomSampleGenerator();
        CorporateTenant corporateTenantBack = getCorporateTenantRandomSampleGenerator();

        apiKey.setTenant(corporateTenantBack);
        assertThat(apiKey.getTenant()).isEqualTo(corporateTenantBack);

        apiKey.tenant(null);
        assertThat(apiKey.getTenant()).isNull();
    }
}
