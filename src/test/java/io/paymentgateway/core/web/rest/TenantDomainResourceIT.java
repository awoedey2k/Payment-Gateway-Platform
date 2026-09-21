package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.TenantDomainAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.repository.TenantDomainRepository;
import io.paymentgateway.core.service.dto.TenantDomainDTO;
import io.paymentgateway.core.service.mapper.TenantDomainMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link TenantDomainResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantDomainResourceIT {

    private static final String DEFAULT_CUSTOM_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOM_DOMAIN = "BBBBBBBBBB";

    private static final String DEFAULT_SUPPORTED_LOCALES = "AAAAAAAAAA";
    private static final String UPDATED_SUPPORTED_LOCALES = "BBBBBBBBBB";

    private static final String DEFAULT_DEFAULT_LOCALE = "AAAAAAAAAA";
    private static final String UPDATED_DEFAULT_LOCALE = "BBBBBBBBBB";

    private static final String DEFAULT_FALLBACK_LOCALE = "AAAAAAAAAA";
    private static final String UPDATED_FALLBACK_LOCALE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_VERIFIED = false;
    private static final Boolean UPDATED_IS_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/tenant-domains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TenantDomainRepository tenantDomainRepository;

    @Autowired
    private TenantDomainMapper tenantDomainMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantDomainMockMvc;

    private TenantDomain tenantDomain;

    private TenantDomain insertedTenantDomain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantDomain createEntity(EntityManager em) {
        TenantDomain tenantDomain = new TenantDomain()
            .customDomain(DEFAULT_CUSTOM_DOMAIN)
            .supportedLocales(DEFAULT_SUPPORTED_LOCALES)
            .defaultLocale(DEFAULT_DEFAULT_LOCALE)
            .fallbackLocale(DEFAULT_FALLBACK_LOCALE)
            .isVerified(DEFAULT_IS_VERIFIED);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        tenantDomain.setTenant(corporateTenant);
        return tenantDomain;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantDomain createUpdatedEntity(EntityManager em) {
        TenantDomain updatedTenantDomain = new TenantDomain()
            .customDomain(UPDATED_CUSTOM_DOMAIN)
            .supportedLocales(UPDATED_SUPPORTED_LOCALES)
            .defaultLocale(UPDATED_DEFAULT_LOCALE)
            .fallbackLocale(UPDATED_FALLBACK_LOCALE)
            .isVerified(UPDATED_IS_VERIFIED);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedTenantDomain.setTenant(corporateTenant);
        return updatedTenantDomain;
    }

    @BeforeEach
    void initTest() {
        tenantDomain = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTenantDomain != null) {
            tenantDomainRepository.delete(insertedTenantDomain);
            insertedTenantDomain = null;
        }
    }

    @Test
    @Transactional
    void createTenantDomain() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);
        var returnedTenantDomainDTO = om.readValue(
            restTenantDomainMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDomainDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TenantDomainDTO.class
        );

        // Validate the TenantDomain in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTenantDomain = tenantDomainMapper.toEntity(returnedTenantDomainDTO);
        assertTenantDomainUpdatableFieldsEquals(returnedTenantDomain, getPersistedTenantDomain(returnedTenantDomain));

        insertedTenantDomain = returnedTenantDomain;
    }

    @Test
    @Transactional
    void createTenantDomainWithExistingId() throws Exception {
        // Create the TenantDomain with an existing ID
        tenantDomain.setId(1L);
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantDomainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDomainDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCustomDomainIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDomain.setCustomDomain(null);

        // Create the TenantDomain, which fails.
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        restTenantDomainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDomainDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsVerifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDomain.setIsVerified(null);

        // Create the TenantDomain, which fails.
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        restTenantDomainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDomainDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenantDomains() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantDomain.getId().intValue())))
            .andExpect(jsonPath("$.[*].customDomain").value(hasItem(DEFAULT_CUSTOM_DOMAIN)))
            .andExpect(jsonPath("$.[*].supportedLocales").value(hasItem(DEFAULT_SUPPORTED_LOCALES)))
            .andExpect(jsonPath("$.[*].defaultLocale").value(hasItem(DEFAULT_DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].fallbackLocale").value(hasItem(DEFAULT_FALLBACK_LOCALE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)));
    }

    @Test
    @Transactional
    void getTenantDomain() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get the tenantDomain
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL_ID, tenantDomain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenantDomain.getId().intValue()))
            .andExpect(jsonPath("$.customDomain").value(DEFAULT_CUSTOM_DOMAIN))
            .andExpect(jsonPath("$.supportedLocales").value(DEFAULT_SUPPORTED_LOCALES))
            .andExpect(jsonPath("$.defaultLocale").value(DEFAULT_DEFAULT_LOCALE))
            .andExpect(jsonPath("$.fallbackLocale").value(DEFAULT_FALLBACK_LOCALE))
            .andExpect(jsonPath("$.isVerified").value(DEFAULT_IS_VERIFIED));
    }

    @Test
    @Transactional
    void getTenantDomainsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        Long id = tenantDomain.getId();

        defaultTenantDomainFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTenantDomainFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTenantDomainFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTenantDomainsByCustomDomainIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where customDomain equals to
        defaultTenantDomainFiltering("customDomain.equals=" + DEFAULT_CUSTOM_DOMAIN, "customDomain.equals=" + UPDATED_CUSTOM_DOMAIN);
    }

    @Test
    @Transactional
    void getAllTenantDomainsByCustomDomainIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where customDomain in
        defaultTenantDomainFiltering(
            "customDomain.in=" + DEFAULT_CUSTOM_DOMAIN + "," + UPDATED_CUSTOM_DOMAIN,
            "customDomain.in=" + UPDATED_CUSTOM_DOMAIN
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByCustomDomainIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where customDomain is not null
        defaultTenantDomainFiltering("customDomain.specified=true", "customDomain.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDomainsByCustomDomainContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where customDomain contains
        defaultTenantDomainFiltering("customDomain.contains=" + DEFAULT_CUSTOM_DOMAIN, "customDomain.contains=" + UPDATED_CUSTOM_DOMAIN);
    }

    @Test
    @Transactional
    void getAllTenantDomainsByCustomDomainNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where customDomain does not contain
        defaultTenantDomainFiltering(
            "customDomain.doesNotContain=" + UPDATED_CUSTOM_DOMAIN,
            "customDomain.doesNotContain=" + DEFAULT_CUSTOM_DOMAIN
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsBySupportedLocalesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where supportedLocales equals to
        defaultTenantDomainFiltering(
            "supportedLocales.equals=" + DEFAULT_SUPPORTED_LOCALES,
            "supportedLocales.equals=" + UPDATED_SUPPORTED_LOCALES
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsBySupportedLocalesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where supportedLocales in
        defaultTenantDomainFiltering(
            "supportedLocales.in=" + DEFAULT_SUPPORTED_LOCALES + "," + UPDATED_SUPPORTED_LOCALES,
            "supportedLocales.in=" + UPDATED_SUPPORTED_LOCALES
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsBySupportedLocalesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where supportedLocales is not null
        defaultTenantDomainFiltering("supportedLocales.specified=true", "supportedLocales.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDomainsBySupportedLocalesContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where supportedLocales contains
        defaultTenantDomainFiltering(
            "supportedLocales.contains=" + DEFAULT_SUPPORTED_LOCALES,
            "supportedLocales.contains=" + UPDATED_SUPPORTED_LOCALES
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsBySupportedLocalesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where supportedLocales does not contain
        defaultTenantDomainFiltering(
            "supportedLocales.doesNotContain=" + UPDATED_SUPPORTED_LOCALES,
            "supportedLocales.doesNotContain=" + DEFAULT_SUPPORTED_LOCALES
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByDefaultLocaleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where defaultLocale equals to
        defaultTenantDomainFiltering("defaultLocale.equals=" + DEFAULT_DEFAULT_LOCALE, "defaultLocale.equals=" + UPDATED_DEFAULT_LOCALE);
    }

    @Test
    @Transactional
    void getAllTenantDomainsByDefaultLocaleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where defaultLocale in
        defaultTenantDomainFiltering(
            "defaultLocale.in=" + DEFAULT_DEFAULT_LOCALE + "," + UPDATED_DEFAULT_LOCALE,
            "defaultLocale.in=" + UPDATED_DEFAULT_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByDefaultLocaleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where defaultLocale is not null
        defaultTenantDomainFiltering("defaultLocale.specified=true", "defaultLocale.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDomainsByDefaultLocaleContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where defaultLocale contains
        defaultTenantDomainFiltering(
            "defaultLocale.contains=" + DEFAULT_DEFAULT_LOCALE,
            "defaultLocale.contains=" + UPDATED_DEFAULT_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByDefaultLocaleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where defaultLocale does not contain
        defaultTenantDomainFiltering(
            "defaultLocale.doesNotContain=" + UPDATED_DEFAULT_LOCALE,
            "defaultLocale.doesNotContain=" + DEFAULT_DEFAULT_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByFallbackLocaleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where fallbackLocale equals to
        defaultTenantDomainFiltering(
            "fallbackLocale.equals=" + DEFAULT_FALLBACK_LOCALE,
            "fallbackLocale.equals=" + UPDATED_FALLBACK_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByFallbackLocaleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where fallbackLocale in
        defaultTenantDomainFiltering(
            "fallbackLocale.in=" + DEFAULT_FALLBACK_LOCALE + "," + UPDATED_FALLBACK_LOCALE,
            "fallbackLocale.in=" + UPDATED_FALLBACK_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByFallbackLocaleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where fallbackLocale is not null
        defaultTenantDomainFiltering("fallbackLocale.specified=true", "fallbackLocale.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDomainsByFallbackLocaleContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where fallbackLocale contains
        defaultTenantDomainFiltering(
            "fallbackLocale.contains=" + DEFAULT_FALLBACK_LOCALE,
            "fallbackLocale.contains=" + UPDATED_FALLBACK_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByFallbackLocaleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where fallbackLocale does not contain
        defaultTenantDomainFiltering(
            "fallbackLocale.doesNotContain=" + UPDATED_FALLBACK_LOCALE,
            "fallbackLocale.doesNotContain=" + DEFAULT_FALLBACK_LOCALE
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByIsVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where isVerified equals to
        defaultTenantDomainFiltering("isVerified.equals=" + DEFAULT_IS_VERIFIED, "isVerified.equals=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllTenantDomainsByIsVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where isVerified in
        defaultTenantDomainFiltering(
            "isVerified.in=" + DEFAULT_IS_VERIFIED + "," + UPDATED_IS_VERIFIED,
            "isVerified.in=" + UPDATED_IS_VERIFIED
        );
    }

    @Test
    @Transactional
    void getAllTenantDomainsByIsVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        // Get all the tenantDomainList where isVerified is not null
        defaultTenantDomainFiltering("isVerified.specified=true", "isVerified.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDomainsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            tenantDomainRepository.saveAndFlush(tenantDomain);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        tenantDomain.setTenant(tenant);
        tenantDomainRepository.saveAndFlush(tenantDomain);
        Long tenantId = tenant.getId();
        // Get all the tenantDomainList where tenant equals to tenantId
        defaultTenantDomainShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the tenantDomainList where tenant equals to (tenantId + 1)
        defaultTenantDomainShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultTenantDomainFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTenantDomainShouldBeFound(shouldBeFound);
        defaultTenantDomainShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTenantDomainShouldBeFound(String filter) throws Exception {
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantDomain.getId().intValue())))
            .andExpect(jsonPath("$.[*].customDomain").value(hasItem(DEFAULT_CUSTOM_DOMAIN)))
            .andExpect(jsonPath("$.[*].supportedLocales").value(hasItem(DEFAULT_SUPPORTED_LOCALES)))
            .andExpect(jsonPath("$.[*].defaultLocale").value(hasItem(DEFAULT_DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].fallbackLocale").value(hasItem(DEFAULT_FALLBACK_LOCALE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)));

        // Check, that the count call also returns 1
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTenantDomainShouldNotBeFound(String filter) throws Exception {
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTenantDomainMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTenantDomain() throws Exception {
        // Get the tenantDomain
        restTenantDomainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTenantDomain() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDomain
        TenantDomain updatedTenantDomain = tenantDomainRepository.findById(tenantDomain.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTenantDomain are not directly saved in db
        em.detach(updatedTenantDomain);
        updatedTenantDomain
            .customDomain(UPDATED_CUSTOM_DOMAIN)
            .supportedLocales(UPDATED_SUPPORTED_LOCALES)
            .defaultLocale(UPDATED_DEFAULT_LOCALE)
            .fallbackLocale(UPDATED_FALLBACK_LOCALE)
            .isVerified(UPDATED_IS_VERIFIED);
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(updatedTenantDomain);

        restTenantDomainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDomainDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDomainDTO))
            )
            .andExpect(status().isOk());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTenantDomainToMatchAllProperties(updatedTenantDomain);
    }

    @Test
    @Transactional
    void putNonExistingTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDomainDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDomainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDomainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDomainDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantDomainWithPatch() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDomain using partial update
        TenantDomain partialUpdatedTenantDomain = new TenantDomain();
        partialUpdatedTenantDomain.setId(tenantDomain.getId());

        partialUpdatedTenantDomain
            .customDomain(UPDATED_CUSTOM_DOMAIN)
            .supportedLocales(UPDATED_SUPPORTED_LOCALES)
            .defaultLocale(UPDATED_DEFAULT_LOCALE)
            .isVerified(UPDATED_IS_VERIFIED);

        restTenantDomainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantDomain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantDomain))
            )
            .andExpect(status().isOk());

        // Validate the TenantDomain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantDomainUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTenantDomain, tenantDomain),
            getPersistedTenantDomain(tenantDomain)
        );
    }

    @Test
    @Transactional
    void fullUpdateTenantDomainWithPatch() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDomain using partial update
        TenantDomain partialUpdatedTenantDomain = new TenantDomain();
        partialUpdatedTenantDomain.setId(tenantDomain.getId());

        partialUpdatedTenantDomain
            .customDomain(UPDATED_CUSTOM_DOMAIN)
            .supportedLocales(UPDATED_SUPPORTED_LOCALES)
            .defaultLocale(UPDATED_DEFAULT_LOCALE)
            .fallbackLocale(UPDATED_FALLBACK_LOCALE)
            .isVerified(UPDATED_IS_VERIFIED);

        restTenantDomainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantDomain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantDomain))
            )
            .andExpect(status().isOk());

        // Validate the TenantDomain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantDomainUpdatableFieldsEquals(partialUpdatedTenantDomain, getPersistedTenantDomain(partialUpdatedTenantDomain));
    }

    @Test
    @Transactional
    void patchNonExistingTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantDomainDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDomainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDomainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenantDomain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDomain.setId(longCount.incrementAndGet());

        // Create the TenantDomain
        TenantDomainDTO tenantDomainDTO = tenantDomainMapper.toDto(tenantDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDomainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tenantDomainDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantDomain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenantDomain() throws Exception {
        // Initialize the database
        insertedTenantDomain = tenantDomainRepository.saveAndFlush(tenantDomain);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tenantDomain
        restTenantDomainMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenantDomain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tenantDomainRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TenantDomain getPersistedTenantDomain(TenantDomain tenantDomain) {
        return tenantDomainRepository.findById(tenantDomain.getId()).orElseThrow();
    }

    protected void assertPersistedTenantDomainToMatchAllProperties(TenantDomain expectedTenantDomain) {
        assertTenantDomainAllPropertiesEquals(expectedTenantDomain, getPersistedTenantDomain(expectedTenantDomain));
    }

    protected void assertPersistedTenantDomainToMatchUpdatableProperties(TenantDomain expectedTenantDomain) {
        assertTenantDomainAllUpdatablePropertiesEquals(expectedTenantDomain, getPersistedTenantDomain(expectedTenantDomain));
    }
}
