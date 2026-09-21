package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.CountryPaymentMethodAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.Country;
import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.domain.PaymentMethod;
import io.paymentgateway.core.repository.CountryPaymentMethodRepository;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import io.paymentgateway.core.service.mapper.CountryPaymentMethodMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CountryPaymentMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CountryPaymentMethodResourceIT {

    private static final BigDecimal DEFAULT_MIN_TXN_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_TXN_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_MIN_TXN_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_MAX_TXN_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_TXN_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_MAX_TXN_AMOUNT = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_SUPPORTS_RECURRING = false;
    private static final Boolean UPDATED_SUPPORTS_RECURRING = true;

    private static final Boolean DEFAULT_SUPPORTS_INSTANT_REFUND = false;
    private static final Boolean UPDATED_SUPPORTS_INSTANT_REFUND = true;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/country-payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CountryPaymentMethodRepository countryPaymentMethodRepository;

    @Autowired
    private CountryPaymentMethodMapper countryPaymentMethodMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCountryPaymentMethodMockMvc;

    private CountryPaymentMethod countryPaymentMethod;

    private CountryPaymentMethod insertedCountryPaymentMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CountryPaymentMethod createEntity(EntityManager em) {
        CountryPaymentMethod countryPaymentMethod = new CountryPaymentMethod()
            .minTxnAmount(DEFAULT_MIN_TXN_AMOUNT)
            .maxTxnAmount(DEFAULT_MAX_TXN_AMOUNT)
            .supportsRecurring(DEFAULT_SUPPORTS_RECURRING)
            .supportsInstantRefund(DEFAULT_SUPPORTS_INSTANT_REFUND)
            .isActive(DEFAULT_IS_ACTIVE);
        // Add required entity
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            country = CountryResourceIT.createEntity();
            em.persist(country);
            em.flush();
        } else {
            country = TestUtil.findAll(em, Country.class).getFirst();
        }
        countryPaymentMethod.setCountry(country);
        // Add required entity
        PaymentMethod paymentMethod;
        if (TestUtil.findAll(em, PaymentMethod.class).isEmpty()) {
            paymentMethod = PaymentMethodResourceIT.createEntity();
            em.persist(paymentMethod);
            em.flush();
        } else {
            paymentMethod = TestUtil.findAll(em, PaymentMethod.class).getFirst();
        }
        countryPaymentMethod.setPaymentMethod(paymentMethod);
        return countryPaymentMethod;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CountryPaymentMethod createUpdatedEntity(EntityManager em) {
        CountryPaymentMethod updatedCountryPaymentMethod = new CountryPaymentMethod()
            .minTxnAmount(UPDATED_MIN_TXN_AMOUNT)
            .maxTxnAmount(UPDATED_MAX_TXN_AMOUNT)
            .supportsRecurring(UPDATED_SUPPORTS_RECURRING)
            .supportsInstantRefund(UPDATED_SUPPORTS_INSTANT_REFUND)
            .isActive(UPDATED_IS_ACTIVE);
        // Add required entity
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            country = CountryResourceIT.createUpdatedEntity();
            em.persist(country);
            em.flush();
        } else {
            country = TestUtil.findAll(em, Country.class).getFirst();
        }
        updatedCountryPaymentMethod.setCountry(country);
        // Add required entity
        PaymentMethod paymentMethod;
        if (TestUtil.findAll(em, PaymentMethod.class).isEmpty()) {
            paymentMethod = PaymentMethodResourceIT.createUpdatedEntity();
            em.persist(paymentMethod);
            em.flush();
        } else {
            paymentMethod = TestUtil.findAll(em, PaymentMethod.class).getFirst();
        }
        updatedCountryPaymentMethod.setPaymentMethod(paymentMethod);
        return updatedCountryPaymentMethod;
    }

    @BeforeEach
    void initTest() {
        countryPaymentMethod = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCountryPaymentMethod != null) {
            countryPaymentMethodRepository.delete(insertedCountryPaymentMethod);
            insertedCountryPaymentMethod = null;
        }
    }

    @Test
    @Transactional
    void createCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);
        var returnedCountryPaymentMethodDTO = om.readValue(
            restCountryPaymentMethodMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CountryPaymentMethodDTO.class
        );

        // Validate the CountryPaymentMethod in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCountryPaymentMethod = countryPaymentMethodMapper.toEntity(returnedCountryPaymentMethodDTO);
        assertCountryPaymentMethodUpdatableFieldsEquals(
            returnedCountryPaymentMethod,
            getPersistedCountryPaymentMethod(returnedCountryPaymentMethod)
        );

        insertedCountryPaymentMethod = returnedCountryPaymentMethod;
    }

    @Test
    @Transactional
    void createCountryPaymentMethodWithExistingId() throws Exception {
        // Create the CountryPaymentMethod with an existing ID
        countryPaymentMethod.setId(1L);
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMinTxnAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        countryPaymentMethod.setMinTxnAmount(null);

        // Create the CountryPaymentMethod, which fails.
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxTxnAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        countryPaymentMethod.setMaxTxnAmount(null);

        // Create the CountryPaymentMethod, which fails.
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSupportsRecurringIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        countryPaymentMethod.setSupportsRecurring(null);

        // Create the CountryPaymentMethod, which fails.
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSupportsInstantRefundIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        countryPaymentMethod.setSupportsInstantRefund(null);

        // Create the CountryPaymentMethod, which fails.
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        countryPaymentMethod.setIsActive(null);

        // Create the CountryPaymentMethod, which fails.
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethods() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(countryPaymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].minTxnAmount").value(hasItem(sameNumber(DEFAULT_MIN_TXN_AMOUNT))))
            .andExpect(jsonPath("$.[*].maxTxnAmount").value(hasItem(sameNumber(DEFAULT_MAX_TXN_AMOUNT))))
            .andExpect(jsonPath("$.[*].supportsRecurring").value(hasItem(DEFAULT_SUPPORTS_RECURRING)))
            .andExpect(jsonPath("$.[*].supportsInstantRefund").value(hasItem(DEFAULT_SUPPORTS_INSTANT_REFUND)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getCountryPaymentMethod() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get the countryPaymentMethod
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL_ID, countryPaymentMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(countryPaymentMethod.getId().intValue()))
            .andExpect(jsonPath("$.minTxnAmount").value(sameNumber(DEFAULT_MIN_TXN_AMOUNT)))
            .andExpect(jsonPath("$.maxTxnAmount").value(sameNumber(DEFAULT_MAX_TXN_AMOUNT)))
            .andExpect(jsonPath("$.supportsRecurring").value(DEFAULT_SUPPORTS_RECURRING))
            .andExpect(jsonPath("$.supportsInstantRefund").value(DEFAULT_SUPPORTS_INSTANT_REFUND))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getCountryPaymentMethodsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        Long id = countryPaymentMethod.getId();

        defaultCountryPaymentMethodFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCountryPaymentMethodFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCountryPaymentMethodFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount equals to
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.equals=" + DEFAULT_MIN_TXN_AMOUNT,
            "minTxnAmount.equals=" + UPDATED_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount in
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.in=" + DEFAULT_MIN_TXN_AMOUNT + "," + UPDATED_MIN_TXN_AMOUNT,
            "minTxnAmount.in=" + UPDATED_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount is not null
        defaultCountryPaymentMethodFiltering("minTxnAmount.specified=true", "minTxnAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount is greater than or equal to
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.greaterThanOrEqual=" + DEFAULT_MIN_TXN_AMOUNT,
            "minTxnAmount.greaterThanOrEqual=" + UPDATED_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount is less than or equal to
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.lessThanOrEqual=" + DEFAULT_MIN_TXN_AMOUNT,
            "minTxnAmount.lessThanOrEqual=" + SMALLER_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount is less than
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.lessThan=" + UPDATED_MIN_TXN_AMOUNT,
            "minTxnAmount.lessThan=" + DEFAULT_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMinTxnAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where minTxnAmount is greater than
        defaultCountryPaymentMethodFiltering(
            "minTxnAmount.greaterThan=" + SMALLER_MIN_TXN_AMOUNT,
            "minTxnAmount.greaterThan=" + DEFAULT_MIN_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount equals to
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.equals=" + DEFAULT_MAX_TXN_AMOUNT,
            "maxTxnAmount.equals=" + UPDATED_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount in
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.in=" + DEFAULT_MAX_TXN_AMOUNT + "," + UPDATED_MAX_TXN_AMOUNT,
            "maxTxnAmount.in=" + UPDATED_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount is not null
        defaultCountryPaymentMethodFiltering("maxTxnAmount.specified=true", "maxTxnAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount is greater than or equal to
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.greaterThanOrEqual=" + DEFAULT_MAX_TXN_AMOUNT,
            "maxTxnAmount.greaterThanOrEqual=" + UPDATED_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount is less than or equal to
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.lessThanOrEqual=" + DEFAULT_MAX_TXN_AMOUNT,
            "maxTxnAmount.lessThanOrEqual=" + SMALLER_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount is less than
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.lessThan=" + UPDATED_MAX_TXN_AMOUNT,
            "maxTxnAmount.lessThan=" + DEFAULT_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByMaxTxnAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where maxTxnAmount is greater than
        defaultCountryPaymentMethodFiltering(
            "maxTxnAmount.greaterThan=" + SMALLER_MAX_TXN_AMOUNT,
            "maxTxnAmount.greaterThan=" + DEFAULT_MAX_TXN_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsRecurringIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsRecurring equals to
        defaultCountryPaymentMethodFiltering(
            "supportsRecurring.equals=" + DEFAULT_SUPPORTS_RECURRING,
            "supportsRecurring.equals=" + UPDATED_SUPPORTS_RECURRING
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsRecurringIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsRecurring in
        defaultCountryPaymentMethodFiltering(
            "supportsRecurring.in=" + DEFAULT_SUPPORTS_RECURRING + "," + UPDATED_SUPPORTS_RECURRING,
            "supportsRecurring.in=" + UPDATED_SUPPORTS_RECURRING
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsRecurringIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsRecurring is not null
        defaultCountryPaymentMethodFiltering("supportsRecurring.specified=true", "supportsRecurring.specified=false");
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsInstantRefundIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsInstantRefund equals to
        defaultCountryPaymentMethodFiltering(
            "supportsInstantRefund.equals=" + DEFAULT_SUPPORTS_INSTANT_REFUND,
            "supportsInstantRefund.equals=" + UPDATED_SUPPORTS_INSTANT_REFUND
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsInstantRefundIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsInstantRefund in
        defaultCountryPaymentMethodFiltering(
            "supportsInstantRefund.in=" + DEFAULT_SUPPORTS_INSTANT_REFUND + "," + UPDATED_SUPPORTS_INSTANT_REFUND,
            "supportsInstantRefund.in=" + UPDATED_SUPPORTS_INSTANT_REFUND
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsBySupportsInstantRefundIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where supportsInstantRefund is not null
        defaultCountryPaymentMethodFiltering("supportsInstantRefund.specified=true", "supportsInstantRefund.specified=false");
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where isActive equals to
        defaultCountryPaymentMethodFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where isActive in
        defaultCountryPaymentMethodFiltering(
            "isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE,
            "isActive.in=" + UPDATED_IS_ACTIVE
        );
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        // Get all the countryPaymentMethodList where isActive is not null
        defaultCountryPaymentMethodFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);
            country = CountryResourceIT.createEntity();
        } else {
            country = TestUtil.findAll(em, Country.class).getFirst();
        }
        em.persist(country);
        em.flush();
        countryPaymentMethod.setCountry(country);
        countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);
        Long countryId = country.getId();
        // Get all the countryPaymentMethodList where country equals to countryId
        defaultCountryPaymentMethodShouldBeFound("countryId.equals=" + countryId);

        // Get all the countryPaymentMethodList where country equals to (countryId + 1)
        defaultCountryPaymentMethodShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    @Test
    @Transactional
    void getAllCountryPaymentMethodsByPaymentMethodIsEqualToSomething() throws Exception {
        PaymentMethod paymentMethod;
        if (TestUtil.findAll(em, PaymentMethod.class).isEmpty()) {
            countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);
            paymentMethod = PaymentMethodResourceIT.createEntity();
        } else {
            paymentMethod = TestUtil.findAll(em, PaymentMethod.class).getFirst();
        }
        em.persist(paymentMethod);
        em.flush();
        countryPaymentMethod.setPaymentMethod(paymentMethod);
        countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);
        Long paymentMethodId = paymentMethod.getId();
        // Get all the countryPaymentMethodList where paymentMethod equals to paymentMethodId
        defaultCountryPaymentMethodShouldBeFound("paymentMethodId.equals=" + paymentMethodId);

        // Get all the countryPaymentMethodList where paymentMethod equals to (paymentMethodId + 1)
        defaultCountryPaymentMethodShouldNotBeFound("paymentMethodId.equals=" + (paymentMethodId + 1));
    }

    private void defaultCountryPaymentMethodFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCountryPaymentMethodShouldBeFound(shouldBeFound);
        defaultCountryPaymentMethodShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCountryPaymentMethodShouldBeFound(String filter) throws Exception {
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(countryPaymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].minTxnAmount").value(hasItem(sameNumber(DEFAULT_MIN_TXN_AMOUNT))))
            .andExpect(jsonPath("$.[*].maxTxnAmount").value(hasItem(sameNumber(DEFAULT_MAX_TXN_AMOUNT))))
            .andExpect(jsonPath("$.[*].supportsRecurring").value(hasItem(DEFAULT_SUPPORTS_RECURRING)))
            .andExpect(jsonPath("$.[*].supportsInstantRefund").value(hasItem(DEFAULT_SUPPORTS_INSTANT_REFUND)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCountryPaymentMethodShouldNotBeFound(String filter) throws Exception {
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCountryPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCountryPaymentMethod() throws Exception {
        // Get the countryPaymentMethod
        restCountryPaymentMethodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCountryPaymentMethod() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the countryPaymentMethod
        CountryPaymentMethod updatedCountryPaymentMethod = countryPaymentMethodRepository
            .findById(countryPaymentMethod.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCountryPaymentMethod are not directly saved in db
        em.detach(updatedCountryPaymentMethod);
        updatedCountryPaymentMethod
            .minTxnAmount(UPDATED_MIN_TXN_AMOUNT)
            .maxTxnAmount(UPDATED_MAX_TXN_AMOUNT)
            .supportsRecurring(UPDATED_SUPPORTS_RECURRING)
            .supportsInstantRefund(UPDATED_SUPPORTS_INSTANT_REFUND)
            .isActive(UPDATED_IS_ACTIVE);
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(updatedCountryPaymentMethod);

        restCountryPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryPaymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isOk());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCountryPaymentMethodToMatchAllProperties(updatedCountryPaymentMethod);
    }

    @Test
    @Transactional
    void putNonExistingCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryPaymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryPaymentMethodDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCountryPaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the countryPaymentMethod using partial update
        CountryPaymentMethod partialUpdatedCountryPaymentMethod = new CountryPaymentMethod();
        partialUpdatedCountryPaymentMethod.setId(countryPaymentMethod.getId());

        partialUpdatedCountryPaymentMethod
            .minTxnAmount(UPDATED_MIN_TXN_AMOUNT)
            .supportsRecurring(UPDATED_SUPPORTS_RECURRING)
            .supportsInstantRefund(UPDATED_SUPPORTS_INSTANT_REFUND)
            .isActive(UPDATED_IS_ACTIVE);

        restCountryPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountryPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCountryPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the CountryPaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryPaymentMethodUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCountryPaymentMethod, countryPaymentMethod),
            getPersistedCountryPaymentMethod(countryPaymentMethod)
        );
    }

    @Test
    @Transactional
    void fullUpdateCountryPaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the countryPaymentMethod using partial update
        CountryPaymentMethod partialUpdatedCountryPaymentMethod = new CountryPaymentMethod();
        partialUpdatedCountryPaymentMethod.setId(countryPaymentMethod.getId());

        partialUpdatedCountryPaymentMethod
            .minTxnAmount(UPDATED_MIN_TXN_AMOUNT)
            .maxTxnAmount(UPDATED_MAX_TXN_AMOUNT)
            .supportsRecurring(UPDATED_SUPPORTS_RECURRING)
            .supportsInstantRefund(UPDATED_SUPPORTS_INSTANT_REFUND)
            .isActive(UPDATED_IS_ACTIVE);

        restCountryPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountryPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCountryPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the CountryPaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryPaymentMethodUpdatableFieldsEquals(
            partialUpdatedCountryPaymentMethod,
            getPersistedCountryPaymentMethod(partialUpdatedCountryPaymentMethod)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, countryPaymentMethodDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCountryPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        countryPaymentMethod.setId(longCount.incrementAndGet());

        // Create the CountryPaymentMethod
        CountryPaymentMethodDTO countryPaymentMethodDTO = countryPaymentMethodMapper.toDto(countryPaymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(countryPaymentMethodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CountryPaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCountryPaymentMethod() throws Exception {
        // Initialize the database
        insertedCountryPaymentMethod = countryPaymentMethodRepository.saveAndFlush(countryPaymentMethod);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the countryPaymentMethod
        restCountryPaymentMethodMockMvc
            .perform(delete(ENTITY_API_URL_ID, countryPaymentMethod.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return countryPaymentMethodRepository.count();
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

    protected CountryPaymentMethod getPersistedCountryPaymentMethod(CountryPaymentMethod countryPaymentMethod) {
        return countryPaymentMethodRepository.findById(countryPaymentMethod.getId()).orElseThrow();
    }

    protected void assertPersistedCountryPaymentMethodToMatchAllProperties(CountryPaymentMethod expectedCountryPaymentMethod) {
        assertCountryPaymentMethodAllPropertiesEquals(
            expectedCountryPaymentMethod,
            getPersistedCountryPaymentMethod(expectedCountryPaymentMethod)
        );
    }

    protected void assertPersistedCountryPaymentMethodToMatchUpdatableProperties(CountryPaymentMethod expectedCountryPaymentMethod) {
        assertCountryPaymentMethodAllUpdatablePropertiesEquals(
            expectedCountryPaymentMethod,
            getPersistedCountryPaymentMethod(expectedCountryPaymentMethod)
        );
    }
}
