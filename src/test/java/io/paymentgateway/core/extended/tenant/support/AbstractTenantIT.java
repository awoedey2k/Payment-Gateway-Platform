package io.paymentgateway.core.extended.tenant.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.jayway.jsonpath.JsonPath;
import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDirector;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.IdentificationType;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.repository.ExtendedApiKeyRepository;
import io.paymentgateway.core.extended.tenant.repository.ExtendedCorporateTenantRepository;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyHasher;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyService;
import io.paymentgateway.core.repository.CorporateTenantRepository;
import io.paymentgateway.core.repository.TenantDirectorRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Shared plumbing for Tenant module integration tests. Each test creates its own tenants (unique registration numbers) and
 * they are deleted afterwards, so the shared database stays clean for the generated integration tests.
 */
@IntegrationTest
@AutoConfigureMockMvc
@Import(TestClockConfiguration.class)
// The generated test profile pins the connection pool to ONE connection, which serialises every concurrent operation and
// would make any concurrency test pass regardless of locking. These tests need a real pool to prove anything.
@TestPropertySource(properties = "spring.datasource.hikari.maximum-pool-size=20")
public abstract class AbstractTenantIT {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected MutableClock clock;

    @Autowired
    protected ApiKeyService apiKeyService;

    @Autowired
    protected ApiKeyHasher hasher;

    @Autowired
    protected CorporateTenantRepository tenantRepository;

    @Autowired
    protected TenantDirectorRepository directorRepository;

    @Autowired
    protected ExtendedCorporateTenantRepository extendedTenants;

    @Autowired
    protected ExtendedApiKeyRepository extendedKeys;

    @Autowired
    protected TransactionTemplate tx;

    @Autowired
    protected EntityManager em;

    private final List<Long> createdTenantIds = new ArrayList<>();

    @BeforeEach
    void resetClock() {
        clock.reset();
    }

    @AfterEach
    void deleteCreatedTenants() {
        if (createdTenantIds.isEmpty()) {
            return;
        }
        tx.executeWithoutResult(status -> {
            em.createQuery("delete from ApiKey k where k.tenant.id in :ids").setParameter("ids", createdTenantIds).executeUpdate();
            em.createQuery("delete from TenantDirector d where d.tenant.id in :ids").setParameter("ids", createdTenantIds).executeUpdate();
            em.createQuery("delete from TenantDomain d where d.tenant.id in :ids").setParameter("ids", createdTenantIds).executeUpdate();
            em.createQuery("delete from CorporateTenant t where t.id in :ids").setParameter("ids", createdTenantIds).executeUpdate();
        });
        createdTenantIds.clear();
    }

    protected CorporateTenant newTenant(TenantStatus status, KycStatus kycStatus) {
        return newTenant(status, kycStatus, "RC-" + UUID.randomUUID());
    }

    protected CorporateTenant newTenant(TenantStatus status, KycStatus kycStatus, String registrationNumber) {
        CorporateTenant saved = tenantRepository.saveAndFlush(
            new CorporateTenant()
                .legalBusinessName("Apex Technologies Global Limited")
                .businessRegistrationNumber(registrationNumber)
                .taxIdentificationNumber("TIN-1")
                .operatingJurisdiction(CountryCode.NG)
                .status(status)
                .kycStatus(kycStatus)
                .createdAt(clock.instant())
        );
        createdTenantIds.add(saved.getId());
        return saved;
    }

    protected CorporateTenant activeTenant() {
        return newTenant(TenantStatus.ACTIVE, KycStatus.APPROVED);
    }

    protected TenantDirector addDirector(CorporateTenant tenant, String fullName) {
        return directorRepository.saveAndFlush(
            new TenantDirector()
                .fullName(fullName)
                .dateOfBirth(LocalDate.of(1984, 6, 15))
                .nationality(CountryCode.NG)
                .identificationType(IdentificationType.NATIONAL_ID)
                .identificationNumber("NIN-1")
                .tenant(tenant)
        );
    }

    protected CorporateTenant reload(CorporateTenant tenant) {
        return tenantRepository.findById(tenant.getId()).orElseThrow();
    }

    /** Registers a tenant created outside {@link #newTenant} for cleanup. */
    protected void track(Long tenantId) {
        createdTenantIds.add(tenantId);
    }

    // ---- HTTP helpers -------------------------------------------------------------------------------------------

    private static RequestPostProcessor staff(String authority) {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("staff").authorities(
            new SimpleGrantedAuthority(authority)
        );
    }

    protected static RequestPostProcessor admin() {
        return staff("ROLE_ADMIN");
    }

    protected static RequestPostProcessor plainUser() {
        return staff("ROLE_USER");
    }

    protected MockHttpServletRequestBuilder adminPost(String url, String jsonBody) {
        return post(url).with(admin()).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
    }

    protected MockHttpServletRequestBuilder whoAmI(String secret) {
        return get("/api/v1/whoami").header("Authorization", "Bearer " + secret);
    }

    protected static String read(MvcResult result, String jsonPath) throws Exception {
        return String.valueOf((Object) JsonPath.read(result.getResponse().getContentAsString(), jsonPath));
    }

    /** Issues a key through the staff endpoint and returns {@code [id, secret]}. */
    protected String[] issueViaApi(Long tenantId, ApiEnvironment environment) throws Exception {
        MvcResult result = mvc
            .perform(adminPost("/api/extended/tenants/" + tenantId + "/api-keys", "{\"environment\":\"" + environment + "\"}"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
            .andReturn();
        return new String[] { read(result, "$.id"), read(result, "$.secret") };
    }
}
