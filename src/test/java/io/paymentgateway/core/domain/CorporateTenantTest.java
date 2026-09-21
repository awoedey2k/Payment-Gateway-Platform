package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.ApiKeyTestSamples.*;
import static io.paymentgateway.core.domain.CorporateTenantTestSamples.*;
import static io.paymentgateway.core.domain.TenantDirectorTestSamples.*;
import static io.paymentgateway.core.domain.TenantDomainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CorporateTenantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CorporateTenant.class);
        CorporateTenant corporateTenant1 = getCorporateTenantSample1();
        CorporateTenant corporateTenant2 = new CorporateTenant();
        assertThat(corporateTenant1).isNotEqualTo(corporateTenant2);

        corporateTenant2.setId(corporateTenant1.getId());
        assertThat(corporateTenant1).isEqualTo(corporateTenant2);

        corporateTenant2 = getCorporateTenantSample2();
        assertThat(corporateTenant1).isNotEqualTo(corporateTenant2);
    }

    @Test
    void tenantDirectorTest() {
        CorporateTenant corporateTenant = getCorporateTenantRandomSampleGenerator();
        TenantDirector tenantDirectorBack = getTenantDirectorRandomSampleGenerator();

        corporateTenant.addTenantDirector(tenantDirectorBack);
        assertThat(corporateTenant.getTenantDirectors()).containsOnly(tenantDirectorBack);
        assertThat(tenantDirectorBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.removeTenantDirector(tenantDirectorBack);
        assertThat(corporateTenant.getTenantDirectors()).doesNotContain(tenantDirectorBack);
        assertThat(tenantDirectorBack.getTenant()).isNull();

        corporateTenant.tenantDirectors(new HashSet<>(Set.of(tenantDirectorBack)));
        assertThat(corporateTenant.getTenantDirectors()).containsOnly(tenantDirectorBack);
        assertThat(tenantDirectorBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.setTenantDirectors(new HashSet<>());
        assertThat(corporateTenant.getTenantDirectors()).doesNotContain(tenantDirectorBack);
        assertThat(tenantDirectorBack.getTenant()).isNull();
    }

    @Test
    void apiKeyTest() {
        CorporateTenant corporateTenant = getCorporateTenantRandomSampleGenerator();
        ApiKey apiKeyBack = getApiKeyRandomSampleGenerator();

        corporateTenant.addApiKey(apiKeyBack);
        assertThat(corporateTenant.getApiKeys()).containsOnly(apiKeyBack);
        assertThat(apiKeyBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.removeApiKey(apiKeyBack);
        assertThat(corporateTenant.getApiKeys()).doesNotContain(apiKeyBack);
        assertThat(apiKeyBack.getTenant()).isNull();

        corporateTenant.apiKeys(new HashSet<>(Set.of(apiKeyBack)));
        assertThat(corporateTenant.getApiKeys()).containsOnly(apiKeyBack);
        assertThat(apiKeyBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.setApiKeys(new HashSet<>());
        assertThat(corporateTenant.getApiKeys()).doesNotContain(apiKeyBack);
        assertThat(apiKeyBack.getTenant()).isNull();
    }

    @Test
    void tenantDomainTest() {
        CorporateTenant corporateTenant = getCorporateTenantRandomSampleGenerator();
        TenantDomain tenantDomainBack = getTenantDomainRandomSampleGenerator();

        corporateTenant.addTenantDomain(tenantDomainBack);
        assertThat(corporateTenant.getTenantDomains()).containsOnly(tenantDomainBack);
        assertThat(tenantDomainBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.removeTenantDomain(tenantDomainBack);
        assertThat(corporateTenant.getTenantDomains()).doesNotContain(tenantDomainBack);
        assertThat(tenantDomainBack.getTenant()).isNull();

        corporateTenant.tenantDomains(new HashSet<>(Set.of(tenantDomainBack)));
        assertThat(corporateTenant.getTenantDomains()).containsOnly(tenantDomainBack);
        assertThat(tenantDomainBack.getTenant()).isEqualTo(corporateTenant);

        corporateTenant.setTenantDomains(new HashSet<>());
        assertThat(corporateTenant.getTenantDomains()).doesNotContain(tenantDomainBack);
        assertThat(tenantDomainBack.getTenant()).isNull();
    }
}
