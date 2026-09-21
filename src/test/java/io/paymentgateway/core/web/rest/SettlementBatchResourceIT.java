package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.SettlementBatchAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.SettlementBatch;
import io.paymentgateway.core.domain.enumeration.SettlementBatchStatus;
import io.paymentgateway.core.repository.SettlementBatchRepository;
import io.paymentgateway.core.service.dto.SettlementBatchDTO;
import io.paymentgateway.core.service.mapper.SettlementBatchMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SettlementBatchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SettlementBatchResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final SettlementBatchStatus DEFAULT_STATUS = SettlementBatchStatus.SCHEDULED;
    private static final SettlementBatchStatus UPDATED_STATUS = SettlementBatchStatus.PROCESSING;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_SCHEDULED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/settlement-batches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SettlementBatchRepository settlementBatchRepository;

    @Autowired
    private SettlementBatchMapper settlementBatchMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSettlementBatchMockMvc;

    private SettlementBatch settlementBatch;

    private SettlementBatch insertedSettlementBatch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SettlementBatch createEntity(EntityManager em) {
        SettlementBatch settlementBatch = new SettlementBatch()
            .reference(DEFAULT_REFERENCE)
            .status(DEFAULT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .scheduledAt(DEFAULT_SCHEDULED_AT)
            .completedAt(DEFAULT_COMPLETED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        settlementBatch.setTenant(corporateTenant);
        return settlementBatch;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SettlementBatch createUpdatedEntity(EntityManager em) {
        SettlementBatch updatedSettlementBatch = new SettlementBatch()
            .reference(UPDATED_REFERENCE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedSettlementBatch.setTenant(corporateTenant);
        return updatedSettlementBatch;
    }

    @BeforeEach
    void initTest() {
        settlementBatch = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSettlementBatch != null) {
            settlementBatchRepository.delete(insertedSettlementBatch);
            insertedSettlementBatch = null;
        }
    }

    @Test
    @Transactional
    void createSettlementBatch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);
        var returnedSettlementBatchDTO = om.readValue(
            restSettlementBatchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SettlementBatchDTO.class
        );

        // Validate the SettlementBatch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSettlementBatch = settlementBatchMapper.toEntity(returnedSettlementBatchDTO);
        assertSettlementBatchUpdatableFieldsEquals(returnedSettlementBatch, getPersistedSettlementBatch(returnedSettlementBatch));

        insertedSettlementBatch = returnedSettlementBatch;
    }

    @Test
    @Transactional
    void createSettlementBatchWithExistingId() throws Exception {
        // Create the SettlementBatch with an existing ID
        settlementBatch.setId(1L);
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        settlementBatch.setReference(null);

        // Create the SettlementBatch, which fails.
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        settlementBatch.setStatus(null);

        // Create the SettlementBatch, which fails.
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        settlementBatch.setTotalAmount(null);

        // Create the SettlementBatch, which fails.
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        settlementBatch.setCurrencyCode(null);

        // Create the SettlementBatch, which fails.
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScheduledAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        settlementBatch.setScheduledAt(null);

        // Create the SettlementBatch, which fails.
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        restSettlementBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSettlementBatches() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(settlementBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].scheduledAt").value(hasItem(DEFAULT_SCHEDULED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));
    }

    @Test
    @Transactional
    void getSettlementBatch() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get the settlementBatch
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL_ID, settlementBatch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(settlementBatch.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.scheduledAt").value(DEFAULT_SCHEDULED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()));
    }

    @Test
    @Transactional
    void getSettlementBatchesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        Long id = settlementBatch.getId();

        defaultSettlementBatchFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSettlementBatchFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSettlementBatchFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where reference equals to
        defaultSettlementBatchFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where reference in
        defaultSettlementBatchFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where reference is not null
        defaultSettlementBatchFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where reference contains
        defaultSettlementBatchFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where reference does not contain
        defaultSettlementBatchFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where status equals to
        defaultSettlementBatchFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where status in
        defaultSettlementBatchFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where status is not null
        defaultSettlementBatchFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount equals to
        defaultSettlementBatchFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount in
        defaultSettlementBatchFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount is not null
        defaultSettlementBatchFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount is greater than or equal to
        defaultSettlementBatchFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount is less than or equal to
        defaultSettlementBatchFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount is less than
        defaultSettlementBatchFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where totalAmount is greater than
        defaultSettlementBatchFiltering(
            "totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
            "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where currencyCode equals to
        defaultSettlementBatchFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where currencyCode in
        defaultSettlementBatchFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where currencyCode is not null
        defaultSettlementBatchFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where currencyCode contains
        defaultSettlementBatchFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where currencyCode does not contain
        defaultSettlementBatchFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByScheduledAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where scheduledAt equals to
        defaultSettlementBatchFiltering("scheduledAt.equals=" + DEFAULT_SCHEDULED_AT, "scheduledAt.equals=" + UPDATED_SCHEDULED_AT);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByScheduledAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where scheduledAt in
        defaultSettlementBatchFiltering(
            "scheduledAt.in=" + DEFAULT_SCHEDULED_AT + "," + UPDATED_SCHEDULED_AT,
            "scheduledAt.in=" + UPDATED_SCHEDULED_AT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByScheduledAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where scheduledAt is not null
        defaultSettlementBatchFiltering("scheduledAt.specified=true", "scheduledAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where completedAt equals to
        defaultSettlementBatchFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where completedAt in
        defaultSettlementBatchFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        // Get all the settlementBatchList where completedAt is not null
        defaultSettlementBatchFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSettlementBatchesByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            settlementBatchRepository.saveAndFlush(settlementBatch);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        settlementBatch.setTenant(tenant);
        settlementBatchRepository.saveAndFlush(settlementBatch);
        Long tenantId = tenant.getId();
        // Get all the settlementBatchList where tenant equals to tenantId
        defaultSettlementBatchShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the settlementBatchList where tenant equals to (tenantId + 1)
        defaultSettlementBatchShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultSettlementBatchFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSettlementBatchShouldBeFound(shouldBeFound);
        defaultSettlementBatchShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSettlementBatchShouldBeFound(String filter) throws Exception {
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(settlementBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].scheduledAt").value(hasItem(DEFAULT_SCHEDULED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));

        // Check, that the count call also returns 1
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSettlementBatchShouldNotBeFound(String filter) throws Exception {
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSettlementBatchMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSettlementBatch() throws Exception {
        // Get the settlementBatch
        restSettlementBatchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSettlementBatch() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the settlementBatch
        SettlementBatch updatedSettlementBatch = settlementBatchRepository.findById(settlementBatch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSettlementBatch are not directly saved in db
        em.detach(updatedSettlementBatch);
        updatedSettlementBatch
            .reference(UPDATED_REFERENCE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(updatedSettlementBatch);

        restSettlementBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, settlementBatchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(settlementBatchDTO))
            )
            .andExpect(status().isOk());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSettlementBatchToMatchAllProperties(updatedSettlementBatch);
    }

    @Test
    @Transactional
    void putNonExistingSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, settlementBatchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(settlementBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(settlementBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSettlementBatchWithPatch() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the settlementBatch using partial update
        SettlementBatch partialUpdatedSettlementBatch = new SettlementBatch();
        partialUpdatedSettlementBatch.setId(settlementBatch.getId());

        partialUpdatedSettlementBatch
            .reference(UPDATED_REFERENCE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .completedAt(UPDATED_COMPLETED_AT);

        restSettlementBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSettlementBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSettlementBatch))
            )
            .andExpect(status().isOk());

        // Validate the SettlementBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSettlementBatchUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSettlementBatch, settlementBatch),
            getPersistedSettlementBatch(settlementBatch)
        );
    }

    @Test
    @Transactional
    void fullUpdateSettlementBatchWithPatch() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the settlementBatch using partial update
        SettlementBatch partialUpdatedSettlementBatch = new SettlementBatch();
        partialUpdatedSettlementBatch.setId(settlementBatch.getId());

        partialUpdatedSettlementBatch
            .reference(UPDATED_REFERENCE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .completedAt(UPDATED_COMPLETED_AT);

        restSettlementBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSettlementBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSettlementBatch))
            )
            .andExpect(status().isOk());

        // Validate the SettlementBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSettlementBatchUpdatableFieldsEquals(
            partialUpdatedSettlementBatch,
            getPersistedSettlementBatch(partialUpdatedSettlementBatch)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, settlementBatchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(settlementBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(settlementBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSettlementBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        settlementBatch.setId(longCount.incrementAndGet());

        // Create the SettlementBatch
        SettlementBatchDTO settlementBatchDTO = settlementBatchMapper.toDto(settlementBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettlementBatchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(settlementBatchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SettlementBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSettlementBatch() throws Exception {
        // Initialize the database
        insertedSettlementBatch = settlementBatchRepository.saveAndFlush(settlementBatch);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the settlementBatch
        restSettlementBatchMockMvc
            .perform(delete(ENTITY_API_URL_ID, settlementBatch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return settlementBatchRepository.count();
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

    protected SettlementBatch getPersistedSettlementBatch(SettlementBatch settlementBatch) {
        return settlementBatchRepository.findById(settlementBatch.getId()).orElseThrow();
    }

    protected void assertPersistedSettlementBatchToMatchAllProperties(SettlementBatch expectedSettlementBatch) {
        assertSettlementBatchAllPropertiesEquals(expectedSettlementBatch, getPersistedSettlementBatch(expectedSettlementBatch));
    }

    protected void assertPersistedSettlementBatchToMatchUpdatableProperties(SettlementBatch expectedSettlementBatch) {
        assertSettlementBatchAllUpdatablePropertiesEquals(expectedSettlementBatch, getPersistedSettlementBatch(expectedSettlementBatch));
    }
}
