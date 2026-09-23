package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.CorporateTenantAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.repository.CorporateTenantRepository;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.mapper.CorporateTenantMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link CorporateTenantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CorporateTenantResourceIT {

    private static final String DEFAULT_LEGAL_BUSINESS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LEGAL_BUSINESS_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BUSINESS_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_IDENTIFICATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TAX_IDENTIFICATION_NUMBER = "BBBBBBBBBB";

    private static final CountryCode DEFAULT_OPERATING_JURISDICTION = CountryCode.NG;
    private static final CountryCode UPDATED_OPERATING_JURISDICTION = CountryCode.KE;

    private static final TenantStatus DEFAULT_STATUS = TenantStatus.PENDING_REVIEW;
    private static final TenantStatus UPDATED_STATUS = TenantStatus.ACTIVE;

    private static final KycStatus DEFAULT_KYC_STATUS = KycStatus.NOT_STARTED;
    private static final KycStatus UPDATED_KYC_STATUS = KycStatus.PENDING;

    private static final Integer DEFAULT_RISK_SCORE = 1;
    private static final Integer UPDATED_RISK_SCORE = 2;
    private static final Integer SMALLER_RISK_SCORE = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_ACTIVATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVATED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/corporate-tenants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CorporateTenantRepository corporateTenantRepository;

    @Autowired
    private CorporateTenantMapper corporateTenantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCorporateTenantMockMvc;

    private CorporateTenant corporateTenant;

    private CorporateTenant insertedCorporateTenant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorporateTenant createEntity() {
        return new CorporateTenant()
            .legalBusinessName(DEFAULT_LEGAL_BUSINESS_NAME)
            .businessRegistrationNumber(DEFAULT_BUSINESS_REGISTRATION_NUMBER)
            .taxIdentificationNumber(DEFAULT_TAX_IDENTIFICATION_NUMBER)
            .operatingJurisdiction(DEFAULT_OPERATING_JURISDICTION)
            .status(DEFAULT_STATUS)
            .kycStatus(DEFAULT_KYC_STATUS)
            .riskScore(DEFAULT_RISK_SCORE)
            .createdAt(DEFAULT_CREATED_AT)
            .activatedAt(DEFAULT_ACTIVATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorporateTenant createUpdatedEntity() {
        return new CorporateTenant()
            .legalBusinessName(UPDATED_LEGAL_BUSINESS_NAME)
            .businessRegistrationNumber(UPDATED_BUSINESS_REGISTRATION_NUMBER)
            .taxIdentificationNumber(UPDATED_TAX_IDENTIFICATION_NUMBER)
            .operatingJurisdiction(UPDATED_OPERATING_JURISDICTION)
            .status(UPDATED_STATUS)
            .kycStatus(UPDATED_KYC_STATUS)
            .riskScore(UPDATED_RISK_SCORE)
            .createdAt(UPDATED_CREATED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);
    }

    @BeforeEach
    void initTest() {
        corporateTenant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCorporateTenant != null) {
            corporateTenantRepository.delete(insertedCorporateTenant);
            insertedCorporateTenant = null;
        }
    }

    @Test
    @Transactional
    void createCorporateTenant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);
        var returnedCorporateTenantDTO = om.readValue(
            restCorporateTenantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CorporateTenantDTO.class
        );

        // Validate the CorporateTenant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCorporateTenant = corporateTenantMapper.toEntity(returnedCorporateTenantDTO);
        assertCorporateTenantUpdatableFieldsEquals(returnedCorporateTenant, getPersistedCorporateTenant(returnedCorporateTenant));

        insertedCorporateTenant = returnedCorporateTenant;
    }

    @Test
    @Transactional
    void createCorporateTenantWithExistingId() throws Exception {
        // Create the CorporateTenant with an existing ID
        corporateTenant.setId(1L);
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLegalBusinessNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setLegalBusinessName(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBusinessRegistrationNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setBusinessRegistrationNumber(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTaxIdentificationNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setTaxIdentificationNumber(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperatingJurisdictionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setOperatingJurisdiction(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setStatus(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkKycStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setKycStatus(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        corporateTenant.setCreatedAt(null);

        // Create the CorporateTenant, which fails.
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        restCorporateTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCorporateTenants() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(corporateTenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].legalBusinessName").value(hasItem(DEFAULT_LEGAL_BUSINESS_NAME)))
            .andExpect(jsonPath("$.[*].businessRegistrationNumber").value(hasItem(DEFAULT_BUSINESS_REGISTRATION_NUMBER)))
            .andExpect(jsonPath("$.[*].taxIdentificationNumber").value(hasItem(DEFAULT_TAX_IDENTIFICATION_NUMBER)))
            .andExpect(jsonPath("$.[*].operatingJurisdiction").value(hasItem(DEFAULT_OPERATING_JURISDICTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].kycStatus").value(hasItem(DEFAULT_KYC_STATUS.toString())))
            .andExpect(jsonPath("$.[*].riskScore").value(hasItem(DEFAULT_RISK_SCORE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].activatedAt").value(hasItem(DEFAULT_ACTIVATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCorporateTenant() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get the corporateTenant
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL_ID, corporateTenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(corporateTenant.getId().intValue()))
            .andExpect(jsonPath("$.legalBusinessName").value(DEFAULT_LEGAL_BUSINESS_NAME))
            .andExpect(jsonPath("$.businessRegistrationNumber").value(DEFAULT_BUSINESS_REGISTRATION_NUMBER))
            .andExpect(jsonPath("$.taxIdentificationNumber").value(DEFAULT_TAX_IDENTIFICATION_NUMBER))
            .andExpect(jsonPath("$.operatingJurisdiction").value(DEFAULT_OPERATING_JURISDICTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.kycStatus").value(DEFAULT_KYC_STATUS.toString()))
            .andExpect(jsonPath("$.riskScore").value(DEFAULT_RISK_SCORE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.activatedAt").value(DEFAULT_ACTIVATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCorporateTenantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        Long id = corporateTenant.getId();

        defaultCorporateTenantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCorporateTenantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCorporateTenantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByLegalBusinessNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where legalBusinessName equals to
        defaultCorporateTenantFiltering(
            "legalBusinessName.equals=" + DEFAULT_LEGAL_BUSINESS_NAME,
            "legalBusinessName.equals=" + UPDATED_LEGAL_BUSINESS_NAME
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByLegalBusinessNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where legalBusinessName in
        defaultCorporateTenantFiltering(
            "legalBusinessName.in=" + DEFAULT_LEGAL_BUSINESS_NAME + "," + UPDATED_LEGAL_BUSINESS_NAME,
            "legalBusinessName.in=" + UPDATED_LEGAL_BUSINESS_NAME
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByLegalBusinessNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where legalBusinessName is not null
        defaultCorporateTenantFiltering("legalBusinessName.specified=true", "legalBusinessName.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByLegalBusinessNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where legalBusinessName contains
        defaultCorporateTenantFiltering(
            "legalBusinessName.contains=" + DEFAULT_LEGAL_BUSINESS_NAME,
            "legalBusinessName.contains=" + UPDATED_LEGAL_BUSINESS_NAME
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByLegalBusinessNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where legalBusinessName does not contain
        defaultCorporateTenantFiltering(
            "legalBusinessName.doesNotContain=" + UPDATED_LEGAL_BUSINESS_NAME,
            "legalBusinessName.doesNotContain=" + DEFAULT_LEGAL_BUSINESS_NAME
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByBusinessRegistrationNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where businessRegistrationNumber equals to
        defaultCorporateTenantFiltering(
            "businessRegistrationNumber.equals=" + DEFAULT_BUSINESS_REGISTRATION_NUMBER,
            "businessRegistrationNumber.equals=" + UPDATED_BUSINESS_REGISTRATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByBusinessRegistrationNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where businessRegistrationNumber in
        defaultCorporateTenantFiltering(
            "businessRegistrationNumber.in=" + DEFAULT_BUSINESS_REGISTRATION_NUMBER + "," + UPDATED_BUSINESS_REGISTRATION_NUMBER,
            "businessRegistrationNumber.in=" + UPDATED_BUSINESS_REGISTRATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByBusinessRegistrationNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where businessRegistrationNumber is not null
        defaultCorporateTenantFiltering("businessRegistrationNumber.specified=true", "businessRegistrationNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByBusinessRegistrationNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where businessRegistrationNumber contains
        defaultCorporateTenantFiltering(
            "businessRegistrationNumber.contains=" + DEFAULT_BUSINESS_REGISTRATION_NUMBER,
            "businessRegistrationNumber.contains=" + UPDATED_BUSINESS_REGISTRATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByBusinessRegistrationNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where businessRegistrationNumber does not contain
        defaultCorporateTenantFiltering(
            "businessRegistrationNumber.doesNotContain=" + UPDATED_BUSINESS_REGISTRATION_NUMBER,
            "businessRegistrationNumber.doesNotContain=" + DEFAULT_BUSINESS_REGISTRATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByTaxIdentificationNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where taxIdentificationNumber equals to
        defaultCorporateTenantFiltering(
            "taxIdentificationNumber.equals=" + DEFAULT_TAX_IDENTIFICATION_NUMBER,
            "taxIdentificationNumber.equals=" + UPDATED_TAX_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByTaxIdentificationNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where taxIdentificationNumber in
        defaultCorporateTenantFiltering(
            "taxIdentificationNumber.in=" + DEFAULT_TAX_IDENTIFICATION_NUMBER + "," + UPDATED_TAX_IDENTIFICATION_NUMBER,
            "taxIdentificationNumber.in=" + UPDATED_TAX_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByTaxIdentificationNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where taxIdentificationNumber is not null
        defaultCorporateTenantFiltering("taxIdentificationNumber.specified=true", "taxIdentificationNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByTaxIdentificationNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where taxIdentificationNumber contains
        defaultCorporateTenantFiltering(
            "taxIdentificationNumber.contains=" + DEFAULT_TAX_IDENTIFICATION_NUMBER,
            "taxIdentificationNumber.contains=" + UPDATED_TAX_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByTaxIdentificationNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where taxIdentificationNumber does not contain
        defaultCorporateTenantFiltering(
            "taxIdentificationNumber.doesNotContain=" + UPDATED_TAX_IDENTIFICATION_NUMBER,
            "taxIdentificationNumber.doesNotContain=" + DEFAULT_TAX_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByOperatingJurisdictionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where operatingJurisdiction equals to
        defaultCorporateTenantFiltering(
            "operatingJurisdiction.equals=" + DEFAULT_OPERATING_JURISDICTION,
            "operatingJurisdiction.equals=" + UPDATED_OPERATING_JURISDICTION
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByOperatingJurisdictionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where operatingJurisdiction in
        defaultCorporateTenantFiltering(
            "operatingJurisdiction.in=" + DEFAULT_OPERATING_JURISDICTION + "," + UPDATED_OPERATING_JURISDICTION,
            "operatingJurisdiction.in=" + UPDATED_OPERATING_JURISDICTION
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByOperatingJurisdictionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where operatingJurisdiction is not null
        defaultCorporateTenantFiltering("operatingJurisdiction.specified=true", "operatingJurisdiction.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where status equals to
        defaultCorporateTenantFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where status in
        defaultCorporateTenantFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where status is not null
        defaultCorporateTenantFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByKycStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where kycStatus equals to
        defaultCorporateTenantFiltering("kycStatus.equals=" + DEFAULT_KYC_STATUS, "kycStatus.equals=" + UPDATED_KYC_STATUS);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByKycStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where kycStatus in
        defaultCorporateTenantFiltering(
            "kycStatus.in=" + DEFAULT_KYC_STATUS + "," + UPDATED_KYC_STATUS,
            "kycStatus.in=" + UPDATED_KYC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByKycStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where kycStatus is not null
        defaultCorporateTenantFiltering("kycStatus.specified=true", "kycStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore equals to
        defaultCorporateTenantFiltering("riskScore.equals=" + DEFAULT_RISK_SCORE, "riskScore.equals=" + UPDATED_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore in
        defaultCorporateTenantFiltering(
            "riskScore.in=" + DEFAULT_RISK_SCORE + "," + UPDATED_RISK_SCORE,
            "riskScore.in=" + UPDATED_RISK_SCORE
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore is not null
        defaultCorporateTenantFiltering("riskScore.specified=true", "riskScore.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore is greater than or equal to
        defaultCorporateTenantFiltering(
            "riskScore.greaterThanOrEqual=" + DEFAULT_RISK_SCORE,
            "riskScore.greaterThanOrEqual=" + UPDATED_RISK_SCORE
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore is less than or equal to
        defaultCorporateTenantFiltering(
            "riskScore.lessThanOrEqual=" + DEFAULT_RISK_SCORE,
            "riskScore.lessThanOrEqual=" + SMALLER_RISK_SCORE
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore is less than
        defaultCorporateTenantFiltering("riskScore.lessThan=" + UPDATED_RISK_SCORE, "riskScore.lessThan=" + DEFAULT_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByRiskScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where riskScore is greater than
        defaultCorporateTenantFiltering("riskScore.greaterThan=" + SMALLER_RISK_SCORE, "riskScore.greaterThan=" + DEFAULT_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where createdAt equals to
        defaultCorporateTenantFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where createdAt in
        defaultCorporateTenantFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where createdAt is not null
        defaultCorporateTenantFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByActivatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where activatedAt equals to
        defaultCorporateTenantFiltering("activatedAt.equals=" + DEFAULT_ACTIVATED_AT, "activatedAt.equals=" + UPDATED_ACTIVATED_AT);
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByActivatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where activatedAt in
        defaultCorporateTenantFiltering(
            "activatedAt.in=" + DEFAULT_ACTIVATED_AT + "," + UPDATED_ACTIVATED_AT,
            "activatedAt.in=" + UPDATED_ACTIVATED_AT
        );
    }

    @Test
    @Transactional
    void getAllCorporateTenantsByActivatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        // Get all the corporateTenantList where activatedAt is not null
        defaultCorporateTenantFiltering("activatedAt.specified=true", "activatedAt.specified=false");
    }

    private void defaultCorporateTenantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCorporateTenantShouldBeFound(shouldBeFound);
        defaultCorporateTenantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCorporateTenantShouldBeFound(String filter) throws Exception {
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(corporateTenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].legalBusinessName").value(hasItem(DEFAULT_LEGAL_BUSINESS_NAME)))
            .andExpect(jsonPath("$.[*].businessRegistrationNumber").value(hasItem(DEFAULT_BUSINESS_REGISTRATION_NUMBER)))
            .andExpect(jsonPath("$.[*].taxIdentificationNumber").value(hasItem(DEFAULT_TAX_IDENTIFICATION_NUMBER)))
            .andExpect(jsonPath("$.[*].operatingJurisdiction").value(hasItem(DEFAULT_OPERATING_JURISDICTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].kycStatus").value(hasItem(DEFAULT_KYC_STATUS.toString())))
            .andExpect(jsonPath("$.[*].riskScore").value(hasItem(DEFAULT_RISK_SCORE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].activatedAt").value(hasItem(DEFAULT_ACTIVATED_AT.toString())));

        // Check, that the count call also returns 1
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCorporateTenantShouldNotBeFound(String filter) throws Exception {
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCorporateTenantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCorporateTenant() throws Exception {
        // Get the corporateTenant
        restCorporateTenantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCorporateTenant() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the corporateTenant
        CorporateTenant updatedCorporateTenant = corporateTenantRepository.findById(corporateTenant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCorporateTenant are not directly saved in db
        em.detach(updatedCorporateTenant);
        updatedCorporateTenant
            .legalBusinessName(UPDATED_LEGAL_BUSINESS_NAME)
            .businessRegistrationNumber(UPDATED_BUSINESS_REGISTRATION_NUMBER)
            .taxIdentificationNumber(UPDATED_TAX_IDENTIFICATION_NUMBER)
            .operatingJurisdiction(UPDATED_OPERATING_JURISDICTION)
            .status(UPDATED_STATUS)
            .kycStatus(UPDATED_KYC_STATUS)
            .riskScore(UPDATED_RISK_SCORE)
            .createdAt(UPDATED_CREATED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(updatedCorporateTenant);

        restCorporateTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, corporateTenantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(corporateTenantDTO))
            )
            .andExpect(status().isOk());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCorporateTenantToMatchAllProperties(updatedCorporateTenant);
    }

    @Test
    @Transactional
    void putNonExistingCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, corporateTenantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(corporateTenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(corporateTenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCorporateTenantWithPatch() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the corporateTenant using partial update
        CorporateTenant partialUpdatedCorporateTenant = new CorporateTenant();
        partialUpdatedCorporateTenant.setId(corporateTenant.getId());

        partialUpdatedCorporateTenant
            .businessRegistrationNumber(UPDATED_BUSINESS_REGISTRATION_NUMBER)
            .taxIdentificationNumber(UPDATED_TAX_IDENTIFICATION_NUMBER)
            .kycStatus(UPDATED_KYC_STATUS)
            .riskScore(UPDATED_RISK_SCORE);

        restCorporateTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorporateTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorporateTenant))
            )
            .andExpect(status().isOk());

        // Validate the CorporateTenant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorporateTenantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCorporateTenant, corporateTenant),
            getPersistedCorporateTenant(corporateTenant)
        );
    }

    @Test
    @Transactional
    void fullUpdateCorporateTenantWithPatch() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the corporateTenant using partial update
        CorporateTenant partialUpdatedCorporateTenant = new CorporateTenant();
        partialUpdatedCorporateTenant.setId(corporateTenant.getId());

        partialUpdatedCorporateTenant
            .legalBusinessName(UPDATED_LEGAL_BUSINESS_NAME)
            .businessRegistrationNumber(UPDATED_BUSINESS_REGISTRATION_NUMBER)
            .taxIdentificationNumber(UPDATED_TAX_IDENTIFICATION_NUMBER)
            .operatingJurisdiction(UPDATED_OPERATING_JURISDICTION)
            .status(UPDATED_STATUS)
            .kycStatus(UPDATED_KYC_STATUS)
            .riskScore(UPDATED_RISK_SCORE)
            .createdAt(UPDATED_CREATED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);

        restCorporateTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorporateTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorporateTenant))
            )
            .andExpect(status().isOk());

        // Validate the CorporateTenant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorporateTenantUpdatableFieldsEquals(
            partialUpdatedCorporateTenant,
            getPersistedCorporateTenant(partialUpdatedCorporateTenant)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, corporateTenantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(corporateTenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(corporateTenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCorporateTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        corporateTenant.setId(longCount.incrementAndGet());

        // Create the CorporateTenant
        CorporateTenantDTO corporateTenantDTO = corporateTenantMapper.toDto(corporateTenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorporateTenantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(corporateTenantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorporateTenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCorporateTenant() throws Exception {
        // Initialize the database
        insertedCorporateTenant = corporateTenantRepository.saveAndFlush(corporateTenant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the corporateTenant
        restCorporateTenantMockMvc
            .perform(delete(ENTITY_API_URL_ID, corporateTenant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return corporateTenantRepository.count();
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

    protected CorporateTenant getPersistedCorporateTenant(CorporateTenant corporateTenant) {
        return corporateTenantRepository.findById(corporateTenant.getId()).orElseThrow();
    }

    protected void assertPersistedCorporateTenantToMatchAllProperties(CorporateTenant expectedCorporateTenant) {
        assertCorporateTenantAllPropertiesEquals(expectedCorporateTenant, getPersistedCorporateTenant(expectedCorporateTenant));
    }

    protected void assertPersistedCorporateTenantToMatchUpdatableProperties(CorporateTenant expectedCorporateTenant) {
        assertCorporateTenantAllUpdatablePropertiesEquals(expectedCorporateTenant, getPersistedCorporateTenant(expectedCorporateTenant));
    }
}
