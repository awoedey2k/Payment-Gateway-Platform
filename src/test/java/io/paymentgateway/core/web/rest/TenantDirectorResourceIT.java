package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.TenantDirectorAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDirector;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.IdentificationType;
import io.paymentgateway.core.repository.TenantDirectorRepository;
import io.paymentgateway.core.service.dto.TenantDirectorDTO;
import io.paymentgateway.core.service.mapper.TenantDirectorMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
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
 * Integration tests for the {@link TenantDirectorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantDirectorResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.parse("2023-12-08");
    private static final LocalDate SMALLER_DATE_OF_BIRTH = LocalDate.ofEpochDay(-1L);

    private static final CountryCode DEFAULT_NATIONALITY = CountryCode.NG;
    private static final CountryCode UPDATED_NATIONALITY = CountryCode.KE;

    private static final IdentificationType DEFAULT_IDENTIFICATION_TYPE = IdentificationType.NATIONAL_ID;
    private static final IdentificationType UPDATED_IDENTIFICATION_TYPE = IdentificationType.PASSPORT;

    private static final String DEFAULT_IDENTIFICATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFICATION_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tenant-directors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TenantDirectorRepository tenantDirectorRepository;

    @Autowired
    private TenantDirectorMapper tenantDirectorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantDirectorMockMvc;

    private TenantDirector tenantDirector;

    private TenantDirector insertedTenantDirector;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantDirector createEntity(EntityManager em) {
        TenantDirector tenantDirector = new TenantDirector()
            .fullName(DEFAULT_FULL_NAME)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .nationality(DEFAULT_NATIONALITY)
            .identificationType(DEFAULT_IDENTIFICATION_TYPE)
            .identificationNumber(DEFAULT_IDENTIFICATION_NUMBER);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        tenantDirector.setTenant(corporateTenant);
        return tenantDirector;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantDirector createUpdatedEntity(EntityManager em) {
        TenantDirector updatedTenantDirector = new TenantDirector()
            .fullName(UPDATED_FULL_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .nationality(UPDATED_NATIONALITY)
            .identificationType(UPDATED_IDENTIFICATION_TYPE)
            .identificationNumber(UPDATED_IDENTIFICATION_NUMBER);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedTenantDirector.setTenant(corporateTenant);
        return updatedTenantDirector;
    }

    @BeforeEach
    void initTest() {
        tenantDirector = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTenantDirector != null) {
            tenantDirectorRepository.delete(insertedTenantDirector);
            insertedTenantDirector = null;
        }
    }

    @Test
    @Transactional
    void createTenantDirector() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);
        var returnedTenantDirectorDTO = om.readValue(
            restTenantDirectorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TenantDirectorDTO.class
        );

        // Validate the TenantDirector in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTenantDirector = tenantDirectorMapper.toEntity(returnedTenantDirectorDTO);
        assertTenantDirectorUpdatableFieldsEquals(returnedTenantDirector, getPersistedTenantDirector(returnedTenantDirector));

        insertedTenantDirector = returnedTenantDirector;
    }

    @Test
    @Transactional
    void createTenantDirectorWithExistingId() throws Exception {
        // Create the TenantDirector with an existing ID
        tenantDirector.setId(1L);
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDirector.setFullName(null);

        // Create the TenantDirector, which fails.
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateOfBirthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDirector.setDateOfBirth(null);

        // Create the TenantDirector, which fails.
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNationalityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDirector.setNationality(null);

        // Create the TenantDirector, which fails.
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIdentificationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDirector.setIdentificationType(null);

        // Create the TenantDirector, which fails.
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIdentificationNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantDirector.setIdentificationNumber(null);

        // Create the TenantDirector, which fails.
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        restTenantDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenantDirectors() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantDirector.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY.toString())))
            .andExpect(jsonPath("$.[*].identificationType").value(hasItem(DEFAULT_IDENTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].identificationNumber").value(hasItem(DEFAULT_IDENTIFICATION_NUMBER)));
    }

    @Test
    @Transactional
    void getTenantDirector() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get the tenantDirector
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL_ID, tenantDirector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenantDirector.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()))
            .andExpect(jsonPath("$.nationality").value(DEFAULT_NATIONALITY.toString()))
            .andExpect(jsonPath("$.identificationType").value(DEFAULT_IDENTIFICATION_TYPE.toString()))
            .andExpect(jsonPath("$.identificationNumber").value(DEFAULT_IDENTIFICATION_NUMBER));
    }

    @Test
    @Transactional
    void getTenantDirectorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        Long id = tenantDirector.getId();

        defaultTenantDirectorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTenantDirectorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTenantDirectorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where fullName equals to
        defaultTenantDirectorFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where fullName in
        defaultTenantDirectorFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where fullName is not null
        defaultTenantDirectorFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where fullName contains
        defaultTenantDirectorFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where fullName does not contain
        defaultTenantDirectorFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth equals to
        defaultTenantDirectorFiltering("dateOfBirth.equals=" + DEFAULT_DATE_OF_BIRTH, "dateOfBirth.equals=" + UPDATED_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth in
        defaultTenantDirectorFiltering(
            "dateOfBirth.in=" + DEFAULT_DATE_OF_BIRTH + "," + UPDATED_DATE_OF_BIRTH,
            "dateOfBirth.in=" + UPDATED_DATE_OF_BIRTH
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth is not null
        defaultTenantDirectorFiltering("dateOfBirth.specified=true", "dateOfBirth.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth is greater than or equal to
        defaultTenantDirectorFiltering(
            "dateOfBirth.greaterThanOrEqual=" + DEFAULT_DATE_OF_BIRTH,
            "dateOfBirth.greaterThanOrEqual=" + UPDATED_DATE_OF_BIRTH
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth is less than or equal to
        defaultTenantDirectorFiltering(
            "dateOfBirth.lessThanOrEqual=" + DEFAULT_DATE_OF_BIRTH,
            "dateOfBirth.lessThanOrEqual=" + SMALLER_DATE_OF_BIRTH
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth is less than
        defaultTenantDirectorFiltering("dateOfBirth.lessThan=" + UPDATED_DATE_OF_BIRTH, "dateOfBirth.lessThan=" + DEFAULT_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByDateOfBirthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where dateOfBirth is greater than
        defaultTenantDirectorFiltering(
            "dateOfBirth.greaterThan=" + SMALLER_DATE_OF_BIRTH,
            "dateOfBirth.greaterThan=" + DEFAULT_DATE_OF_BIRTH
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByNationalityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where nationality equals to
        defaultTenantDirectorFiltering("nationality.equals=" + DEFAULT_NATIONALITY, "nationality.equals=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByNationalityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where nationality in
        defaultTenantDirectorFiltering(
            "nationality.in=" + DEFAULT_NATIONALITY + "," + UPDATED_NATIONALITY,
            "nationality.in=" + UPDATED_NATIONALITY
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByNationalityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where nationality is not null
        defaultTenantDirectorFiltering("nationality.specified=true", "nationality.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationType equals to
        defaultTenantDirectorFiltering(
            "identificationType.equals=" + DEFAULT_IDENTIFICATION_TYPE,
            "identificationType.equals=" + UPDATED_IDENTIFICATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationType in
        defaultTenantDirectorFiltering(
            "identificationType.in=" + DEFAULT_IDENTIFICATION_TYPE + "," + UPDATED_IDENTIFICATION_TYPE,
            "identificationType.in=" + UPDATED_IDENTIFICATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationType is not null
        defaultTenantDirectorFiltering("identificationType.specified=true", "identificationType.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationNumber equals to
        defaultTenantDirectorFiltering(
            "identificationNumber.equals=" + DEFAULT_IDENTIFICATION_NUMBER,
            "identificationNumber.equals=" + UPDATED_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationNumber in
        defaultTenantDirectorFiltering(
            "identificationNumber.in=" + DEFAULT_IDENTIFICATION_NUMBER + "," + UPDATED_IDENTIFICATION_NUMBER,
            "identificationNumber.in=" + UPDATED_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationNumber is not null
        defaultTenantDirectorFiltering("identificationNumber.specified=true", "identificationNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationNumber contains
        defaultTenantDirectorFiltering(
            "identificationNumber.contains=" + DEFAULT_IDENTIFICATION_NUMBER,
            "identificationNumber.contains=" + UPDATED_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByIdentificationNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        // Get all the tenantDirectorList where identificationNumber does not contain
        defaultTenantDirectorFiltering(
            "identificationNumber.doesNotContain=" + UPDATED_IDENTIFICATION_NUMBER,
            "identificationNumber.doesNotContain=" + DEFAULT_IDENTIFICATION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTenantDirectorsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            tenantDirectorRepository.saveAndFlush(tenantDirector);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        tenantDirector.setTenant(tenant);
        tenantDirectorRepository.saveAndFlush(tenantDirector);
        Long tenantId = tenant.getId();
        // Get all the tenantDirectorList where tenant equals to tenantId
        defaultTenantDirectorShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the tenantDirectorList where tenant equals to (tenantId + 1)
        defaultTenantDirectorShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultTenantDirectorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTenantDirectorShouldBeFound(shouldBeFound);
        defaultTenantDirectorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTenantDirectorShouldBeFound(String filter) throws Exception {
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantDirector.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY.toString())))
            .andExpect(jsonPath("$.[*].identificationType").value(hasItem(DEFAULT_IDENTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].identificationNumber").value(hasItem(DEFAULT_IDENTIFICATION_NUMBER)));

        // Check, that the count call also returns 1
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTenantDirectorShouldNotBeFound(String filter) throws Exception {
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTenantDirectorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTenantDirector() throws Exception {
        // Get the tenantDirector
        restTenantDirectorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTenantDirector() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDirector
        TenantDirector updatedTenantDirector = tenantDirectorRepository.findById(tenantDirector.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTenantDirector are not directly saved in db
        em.detach(updatedTenantDirector);
        updatedTenantDirector
            .fullName(UPDATED_FULL_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .nationality(UPDATED_NATIONALITY)
            .identificationType(UPDATED_IDENTIFICATION_TYPE)
            .identificationNumber(UPDATED_IDENTIFICATION_NUMBER);
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(updatedTenantDirector);

        restTenantDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDirectorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDirectorDTO))
            )
            .andExpect(status().isOk());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTenantDirectorToMatchAllProperties(updatedTenantDirector);
    }

    @Test
    @Transactional
    void putNonExistingTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDirectorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDirectorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDirectorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantDirectorWithPatch() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDirector using partial update
        TenantDirector partialUpdatedTenantDirector = new TenantDirector();
        partialUpdatedTenantDirector.setId(tenantDirector.getId());

        partialUpdatedTenantDirector
            .fullName(UPDATED_FULL_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .identificationType(UPDATED_IDENTIFICATION_TYPE)
            .identificationNumber(UPDATED_IDENTIFICATION_NUMBER);

        restTenantDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantDirector))
            )
            .andExpect(status().isOk());

        // Validate the TenantDirector in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantDirectorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTenantDirector, tenantDirector),
            getPersistedTenantDirector(tenantDirector)
        );
    }

    @Test
    @Transactional
    void fullUpdateTenantDirectorWithPatch() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantDirector using partial update
        TenantDirector partialUpdatedTenantDirector = new TenantDirector();
        partialUpdatedTenantDirector.setId(tenantDirector.getId());

        partialUpdatedTenantDirector
            .fullName(UPDATED_FULL_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .nationality(UPDATED_NATIONALITY)
            .identificationType(UPDATED_IDENTIFICATION_TYPE)
            .identificationNumber(UPDATED_IDENTIFICATION_NUMBER);

        restTenantDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantDirector))
            )
            .andExpect(status().isOk());

        // Validate the TenantDirector in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantDirectorUpdatableFieldsEquals(partialUpdatedTenantDirector, getPersistedTenantDirector(partialUpdatedTenantDirector));
    }

    @Test
    @Transactional
    void patchNonExistingTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantDirectorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDirectorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDirectorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenantDirector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantDirector.setId(longCount.incrementAndGet());

        // Create the TenantDirector
        TenantDirectorDTO tenantDirectorDTO = tenantDirectorMapper.toDto(tenantDirector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantDirectorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tenantDirectorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantDirector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenantDirector() throws Exception {
        // Initialize the database
        insertedTenantDirector = tenantDirectorRepository.saveAndFlush(tenantDirector);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tenantDirector
        restTenantDirectorMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenantDirector.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tenantDirectorRepository.count();
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

    protected TenantDirector getPersistedTenantDirector(TenantDirector tenantDirector) {
        return tenantDirectorRepository.findById(tenantDirector.getId()).orElseThrow();
    }

    protected void assertPersistedTenantDirectorToMatchAllProperties(TenantDirector expectedTenantDirector) {
        assertTenantDirectorAllPropertiesEquals(expectedTenantDirector, getPersistedTenantDirector(expectedTenantDirector));
    }

    protected void assertPersistedTenantDirectorToMatchUpdatableProperties(TenantDirector expectedTenantDirector) {
        assertTenantDirectorAllUpdatablePropertiesEquals(expectedTenantDirector, getPersistedTenantDirector(expectedTenantDirector));
    }
}
