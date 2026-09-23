package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.ForexRateAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.ForexRate;
import io.paymentgateway.core.repository.ForexRateRepository;
import io.paymentgateway.core.service.dto.ForexRateDTO;
import io.paymentgateway.core.service.mapper.ForexRateMapper;
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
 * Integration tests for the {@link ForexRateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ForexRateResourceIT {

    private static final String DEFAULT_BASE_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_BASE_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_QUOTE_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_CURRENCY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_RATE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_PLATFORM_SPREAD_BPS = 1;
    private static final Integer UPDATED_PLATFORM_SPREAD_BPS = 2;
    private static final Integer SMALLER_PLATFORM_SPREAD_BPS = 1 - 1;

    private static final Instant DEFAULT_LOCKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LOCKED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/forex-rates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ForexRateRepository forexRateRepository;

    @Autowired
    private ForexRateMapper forexRateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restForexRateMockMvc;

    private ForexRate forexRate;

    private ForexRate insertedForexRate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ForexRate createEntity() {
        return new ForexRate()
            .baseCurrency(DEFAULT_BASE_CURRENCY)
            .quoteCurrency(DEFAULT_QUOTE_CURRENCY)
            .rate(DEFAULT_RATE)
            .platformSpreadBps(DEFAULT_PLATFORM_SPREAD_BPS)
            .lockedAt(DEFAULT_LOCKED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ForexRate createUpdatedEntity() {
        return new ForexRate()
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .rate(UPDATED_RATE)
            .platformSpreadBps(UPDATED_PLATFORM_SPREAD_BPS)
            .lockedAt(UPDATED_LOCKED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
    }

    @BeforeEach
    void initTest() {
        forexRate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedForexRate != null) {
            forexRateRepository.delete(insertedForexRate);
            insertedForexRate = null;
        }
    }

    @Test
    @Transactional
    void createForexRate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);
        var returnedForexRateDTO = om.readValue(
            restForexRateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ForexRateDTO.class
        );

        // Validate the ForexRate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedForexRate = forexRateMapper.toEntity(returnedForexRateDTO);
        assertForexRateUpdatableFieldsEquals(returnedForexRate, getPersistedForexRate(returnedForexRate));

        insertedForexRate = returnedForexRate;
    }

    @Test
    @Transactional
    void createForexRateWithExistingId() throws Exception {
        // Create the ForexRate with an existing ID
        forexRate.setId(1L);
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBaseCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setBaseCurrency(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuoteCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setQuoteCurrency(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setRate(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPlatformSpreadBpsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setPlatformSpreadBps(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLockedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setLockedAt(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forexRate.setExpiresAt(null);

        // Create the ForexRate, which fails.
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        restForexRateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllForexRates() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(forexRate.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseCurrency").value(hasItem(DEFAULT_BASE_CURRENCY)))
            .andExpect(jsonPath("$.[*].quoteCurrency").value(hasItem(DEFAULT_QUOTE_CURRENCY)))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(sameNumber(DEFAULT_RATE))))
            .andExpect(jsonPath("$.[*].platformSpreadBps").value(hasItem(DEFAULT_PLATFORM_SPREAD_BPS)))
            .andExpect(jsonPath("$.[*].lockedAt").value(hasItem(DEFAULT_LOCKED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    @Test
    @Transactional
    void getForexRate() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get the forexRate
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL_ID, forexRate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(forexRate.getId().intValue()))
            .andExpect(jsonPath("$.baseCurrency").value(DEFAULT_BASE_CURRENCY))
            .andExpect(jsonPath("$.quoteCurrency").value(DEFAULT_QUOTE_CURRENCY))
            .andExpect(jsonPath("$.rate").value(sameNumber(DEFAULT_RATE)))
            .andExpect(jsonPath("$.platformSpreadBps").value(DEFAULT_PLATFORM_SPREAD_BPS))
            .andExpect(jsonPath("$.lockedAt").value(DEFAULT_LOCKED_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getForexRatesByIdFiltering() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        Long id = forexRate.getId();

        defaultForexRateFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultForexRateFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultForexRateFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllForexRatesByBaseCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where baseCurrency equals to
        defaultForexRateFiltering("baseCurrency.equals=" + DEFAULT_BASE_CURRENCY, "baseCurrency.equals=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllForexRatesByBaseCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where baseCurrency in
        defaultForexRateFiltering(
            "baseCurrency.in=" + DEFAULT_BASE_CURRENCY + "," + UPDATED_BASE_CURRENCY,
            "baseCurrency.in=" + UPDATED_BASE_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByBaseCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where baseCurrency is not null
        defaultForexRateFiltering("baseCurrency.specified=true", "baseCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllForexRatesByBaseCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where baseCurrency contains
        defaultForexRateFiltering("baseCurrency.contains=" + DEFAULT_BASE_CURRENCY, "baseCurrency.contains=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllForexRatesByBaseCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where baseCurrency does not contain
        defaultForexRateFiltering(
            "baseCurrency.doesNotContain=" + UPDATED_BASE_CURRENCY,
            "baseCurrency.doesNotContain=" + DEFAULT_BASE_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByQuoteCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where quoteCurrency equals to
        defaultForexRateFiltering("quoteCurrency.equals=" + DEFAULT_QUOTE_CURRENCY, "quoteCurrency.equals=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllForexRatesByQuoteCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where quoteCurrency in
        defaultForexRateFiltering(
            "quoteCurrency.in=" + DEFAULT_QUOTE_CURRENCY + "," + UPDATED_QUOTE_CURRENCY,
            "quoteCurrency.in=" + UPDATED_QUOTE_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByQuoteCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where quoteCurrency is not null
        defaultForexRateFiltering("quoteCurrency.specified=true", "quoteCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllForexRatesByQuoteCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where quoteCurrency contains
        defaultForexRateFiltering("quoteCurrency.contains=" + DEFAULT_QUOTE_CURRENCY, "quoteCurrency.contains=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllForexRatesByQuoteCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where quoteCurrency does not contain
        defaultForexRateFiltering(
            "quoteCurrency.doesNotContain=" + UPDATED_QUOTE_CURRENCY,
            "quoteCurrency.doesNotContain=" + DEFAULT_QUOTE_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate equals to
        defaultForexRateFiltering("rate.equals=" + DEFAULT_RATE, "rate.equals=" + UPDATED_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate in
        defaultForexRateFiltering("rate.in=" + DEFAULT_RATE + "," + UPDATED_RATE, "rate.in=" + UPDATED_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate is not null
        defaultForexRateFiltering("rate.specified=true", "rate.specified=false");
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate is greater than or equal to
        defaultForexRateFiltering("rate.greaterThanOrEqual=" + DEFAULT_RATE, "rate.greaterThanOrEqual=" + UPDATED_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate is less than or equal to
        defaultForexRateFiltering("rate.lessThanOrEqual=" + DEFAULT_RATE, "rate.lessThanOrEqual=" + SMALLER_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate is less than
        defaultForexRateFiltering("rate.lessThan=" + UPDATED_RATE, "rate.lessThan=" + DEFAULT_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where rate is greater than
        defaultForexRateFiltering("rate.greaterThan=" + SMALLER_RATE, "rate.greaterThan=" + DEFAULT_RATE);
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps equals to
        defaultForexRateFiltering(
            "platformSpreadBps.equals=" + DEFAULT_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.equals=" + UPDATED_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps in
        defaultForexRateFiltering(
            "platformSpreadBps.in=" + DEFAULT_PLATFORM_SPREAD_BPS + "," + UPDATED_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.in=" + UPDATED_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps is not null
        defaultForexRateFiltering("platformSpreadBps.specified=true", "platformSpreadBps.specified=false");
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps is greater than or equal to
        defaultForexRateFiltering(
            "platformSpreadBps.greaterThanOrEqual=" + DEFAULT_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.greaterThanOrEqual=" + UPDATED_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps is less than or equal to
        defaultForexRateFiltering(
            "platformSpreadBps.lessThanOrEqual=" + DEFAULT_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.lessThanOrEqual=" + SMALLER_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps is less than
        defaultForexRateFiltering(
            "platformSpreadBps.lessThan=" + UPDATED_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.lessThan=" + DEFAULT_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByPlatformSpreadBpsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where platformSpreadBps is greater than
        defaultForexRateFiltering(
            "platformSpreadBps.greaterThan=" + SMALLER_PLATFORM_SPREAD_BPS,
            "platformSpreadBps.greaterThan=" + DEFAULT_PLATFORM_SPREAD_BPS
        );
    }

    @Test
    @Transactional
    void getAllForexRatesByLockedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where lockedAt equals to
        defaultForexRateFiltering("lockedAt.equals=" + DEFAULT_LOCKED_AT, "lockedAt.equals=" + UPDATED_LOCKED_AT);
    }

    @Test
    @Transactional
    void getAllForexRatesByLockedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where lockedAt in
        defaultForexRateFiltering("lockedAt.in=" + DEFAULT_LOCKED_AT + "," + UPDATED_LOCKED_AT, "lockedAt.in=" + UPDATED_LOCKED_AT);
    }

    @Test
    @Transactional
    void getAllForexRatesByLockedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where lockedAt is not null
        defaultForexRateFiltering("lockedAt.specified=true", "lockedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllForexRatesByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where expiresAt equals to
        defaultForexRateFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllForexRatesByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where expiresAt in
        defaultForexRateFiltering("expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT, "expiresAt.in=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllForexRatesByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        // Get all the forexRateList where expiresAt is not null
        defaultForexRateFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    private void defaultForexRateFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultForexRateShouldBeFound(shouldBeFound);
        defaultForexRateShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultForexRateShouldBeFound(String filter) throws Exception {
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(forexRate.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseCurrency").value(hasItem(DEFAULT_BASE_CURRENCY)))
            .andExpect(jsonPath("$.[*].quoteCurrency").value(hasItem(DEFAULT_QUOTE_CURRENCY)))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(sameNumber(DEFAULT_RATE))))
            .andExpect(jsonPath("$.[*].platformSpreadBps").value(hasItem(DEFAULT_PLATFORM_SPREAD_BPS)))
            .andExpect(jsonPath("$.[*].lockedAt").value(hasItem(DEFAULT_LOCKED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));

        // Check, that the count call also returns 1
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultForexRateShouldNotBeFound(String filter) throws Exception {
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restForexRateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingForexRate() throws Exception {
        // Get the forexRate
        restForexRateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingForexRate() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forexRate
        ForexRate updatedForexRate = forexRateRepository.findById(forexRate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedForexRate are not directly saved in db
        em.detach(updatedForexRate);
        updatedForexRate
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .rate(UPDATED_RATE)
            .platformSpreadBps(UPDATED_PLATFORM_SPREAD_BPS)
            .lockedAt(UPDATED_LOCKED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(updatedForexRate);

        restForexRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, forexRateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(forexRateDTO))
            )
            .andExpect(status().isOk());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedForexRateToMatchAllProperties(updatedForexRate);
    }

    @Test
    @Transactional
    void putNonExistingForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, forexRateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(forexRateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(forexRateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateForexRateWithPatch() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forexRate using partial update
        ForexRate partialUpdatedForexRate = new ForexRate();
        partialUpdatedForexRate.setId(forexRate.getId());

        partialUpdatedForexRate
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .rate(UPDATED_RATE)
            .platformSpreadBps(UPDATED_PLATFORM_SPREAD_BPS)
            .lockedAt(UPDATED_LOCKED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);

        restForexRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedForexRate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedForexRate))
            )
            .andExpect(status().isOk());

        // Validate the ForexRate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertForexRateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedForexRate, forexRate),
            getPersistedForexRate(forexRate)
        );
    }

    @Test
    @Transactional
    void fullUpdateForexRateWithPatch() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forexRate using partial update
        ForexRate partialUpdatedForexRate = new ForexRate();
        partialUpdatedForexRate.setId(forexRate.getId());

        partialUpdatedForexRate
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .rate(UPDATED_RATE)
            .platformSpreadBps(UPDATED_PLATFORM_SPREAD_BPS)
            .lockedAt(UPDATED_LOCKED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);

        restForexRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedForexRate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedForexRate))
            )
            .andExpect(status().isOk());

        // Validate the ForexRate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertForexRateUpdatableFieldsEquals(partialUpdatedForexRate, getPersistedForexRate(partialUpdatedForexRate));
    }

    @Test
    @Transactional
    void patchNonExistingForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, forexRateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(forexRateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(forexRateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamForexRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forexRate.setId(longCount.incrementAndGet());

        // Create the ForexRate
        ForexRateDTO forexRateDTO = forexRateMapper.toDto(forexRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForexRateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(forexRateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ForexRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteForexRate() throws Exception {
        // Initialize the database
        insertedForexRate = forexRateRepository.saveAndFlush(forexRate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the forexRate
        restForexRateMockMvc
            .perform(delete(ENTITY_API_URL_ID, forexRate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return forexRateRepository.count();
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

    protected ForexRate getPersistedForexRate(ForexRate forexRate) {
        return forexRateRepository.findById(forexRate.getId()).orElseThrow();
    }

    protected void assertPersistedForexRateToMatchAllProperties(ForexRate expectedForexRate) {
        assertForexRateAllPropertiesEquals(expectedForexRate, getPersistedForexRate(expectedForexRate));
    }

    protected void assertPersistedForexRateToMatchUpdatableProperties(ForexRate expectedForexRate) {
        assertForexRateAllUpdatablePropertiesEquals(expectedForexRate, getPersistedForexRate(expectedForexRate));
    }
}
