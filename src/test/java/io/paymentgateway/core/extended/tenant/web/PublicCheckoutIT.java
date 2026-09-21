package io.paymentgateway.core.extended.tenant.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import io.paymentgateway.core.repository.TenantDomainRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Custom-domain (CNAME) resolution and locale hydration for the checkout page, without any credentials. */
class PublicCheckoutIT extends AbstractTenantIT {

    private static final String URL = "/api/v1/public/checkout-config";

    @Autowired
    private TenantDomainRepository domainRepository;

    private String domainFor(CorporateTenant tenant, boolean verified, String supported, String defaultLocale, String fallback) {
        String host = "pay-" + UUID.randomUUID().toString().substring(0, 8) + ".swiftmarket.test";
        domainRepository.saveAndFlush(
            new TenantDomain()
                .customDomain(host)
                .supportedLocales(supported)
                .defaultLocale(defaultLocale)
                .fallbackLocale(fallback)
                .isVerified(verified)
                .tenant(tenant)
        );
        return host;
    }

    @Test
    void aVerifiedCustomDomainResolvesAndTheBrowserLanguageIsHydrated() throws Exception {
        String host = domainFor(activeTenant(), true, "en_US,fr_SN,pt_AO", "en_US", "en_US");

        mvc.perform(get(URL).header("Host", host).header("Accept-Language", "fr-SN,fr;q=0.9,en;q=0.5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customDomain").value(host))
            .andExpect(jsonPath("$.locale").value("fr_SN"))
            .andExpect(jsonPath("$.supportedLocales[0]").value("en_US"))
            .andExpect(jsonPath("$.supportedLocales.length()").value(3));
    }

    @Test
    void theHostMatchIgnoresCaseAndPort() throws Exception {
        String host = domainFor(activeTenant(), true, "en_US,fr_SN", "en_US", null);
        mvc.perform(get(URL).header("Host", host.toUpperCase() + ":8443"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customDomain").value(host));
    }

    @Test
    void withoutAMatchingLanguageTheDomainDefaultIsUsed() throws Exception {
        String host = domainFor(activeTenant(), true, "en_US,fr_SN", "fr_SN", "en_US");
        mvc.perform(get(URL).header("Host", host).header("Accept-Language", "ja-JP")).andExpect(jsonPath("$.locale").value("fr_SN"));
        mvc.perform(get(URL).header("Host", host)).andExpect(jsonPath("$.locale").value("fr_SN"));
    }

    @Test
    void anUnverifiedDomainDoesNotResolve() throws Exception {
        String host = domainFor(activeTenant(), false, "en_US", "en_US", null);
        mvc.perform(get(URL).header("Host", host)).andExpect(status().isNotFound());
    }

    @Test
    void anUnknownDomainDoesNotResolve() throws Exception {
        mvc.perform(get(URL).header("Host", "nobody.example.test")).andExpect(status().isNotFound());
    }

    @Test
    void theResponseNeverExposesInternalTenantIdentifiers() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED);
        String host = domainFor(tenant, true, "en_US", "en_US", null);
        mvc.perform(get(URL).header("Host", host))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").doesNotExist())
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("\"id\""))));
    }
}
