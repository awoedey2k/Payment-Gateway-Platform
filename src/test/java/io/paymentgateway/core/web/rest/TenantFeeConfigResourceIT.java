package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.TenantFeeConfigAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.domain.TenantFeeConfig;
import io.paymentgateway.core.domain.enumeration.FeeBearer;
import io.paymentgateway.core.repository.TenantFeeConfigRepository;
import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
import io.paymentgateway.core.service.mapper.TenantFeeConfigMapper;
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
 * Integration tests for the {@link TenantFeeConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantFeeConfigResourceIT {

    private static final BigDecimal DEFAULT_FIXED_FEE = new BigDecimal(1);
    private static final BigDecimal UPDATED_FIXED_FEE = new BigDecimal(2);
    private static final BigDecimal SMALLER_FIXED_FEE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_PERCENTAGE_FEE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PERCENTAGE_FEE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PERCENTAGE_FEE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_CAP_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CAP_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_CAP_AMOUNT = new BigDecimal(1 - 1);

    private static final FeeBearer DEFAULT_FEE_BEARER = FeeBearer.MERCHANT;
    private static final FeeBearer UPDATED_FEE_BEARER = FeeBearer.CUSTOMER;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/tenant-fee-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TenantFeeConfigRepository tenantFeeConfigRepository;

    @Autowired
    private TenantFeeConfigMapper tenantFeeConfigMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantFeeConfigMockMvc;

    private TenantFeeConfig tenantFeeConfig;

    private TenantFeeConfig insertedTenantFeeConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantFeeConfig createEntity(EntityManager em) {
        TenantFeeConfig tenantFeeConfig = new TenantFeeConfig()
            .fixedFee(DEFAULT_FIXED_FEE)
            .percentageFee(DEFAULT_PERCENTAGE_FEE)
            .capAmount(DEFAULT_CAP_AMOUNT)
            .feeBearer(DEFAULT_FEE_BEARER)
            .isActive(DEFAULT_IS_ACTIVE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        tenantFeeConfig.setTenant(corporateTenant);
        // Add required entity
        CountryPaymentMethod countryPaymentMethod;
        if (TestUtil.findAll(em, CountryPaymentMethod.class).isEmpty()) {
            countryPaymentMethod = CountryPaymentMethodResourceIT.createEntity(em);
            em.persist(countryPaymentMethod);
            em.flush();
        } else {
            countryPaymentMethod = TestUtil.findAll(em, CountryPaymentMethod.class).getFirst();
        }
        tenantFeeConfig.setCountryPaymentMethod(countryPaymentMethod);
        return tenantFeeConfig;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantFeeConfig createUpdatedEntity(EntityManager em) {
        TenantFeeConfig updatedTenantFeeConfig = new TenantFeeConfig()
            .fixedFee(UPDATED_FIXED_FEE)
            .percentageFee(UPDATED_PERCENTAGE_FEE)
            .capAmount(UPDATED_CAP_AMOUNT)
            .feeBearer(UPDATED_FEE_BEARER)
            .isActive(UPDATED_IS_ACTIVE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedTenantFeeConfig.setTenant(corporateTenant);
        // Add required entity
        CountryPaymentMethod countryPaymentMethod;
        if (TestUtil.findAll(em, CountryPaymentMethod.class).isEmpty()) {
            countryPaymentMethod = CountryPaymentMethodResourceIT.createUpdatedEntity(em);
            em.persist(countryPaymentMethod);
            em.flush();
        } else {
            countryPaymentMethod = TestUtil.findAll(em, CountryPaymentMethod.class).getFirst();
        }
        updatedTenantFeeConfig.setCountryPaymentMethod(countryPaymentMethod);
        return updatedTenantFeeConfig;
    }

    @BeforeEach
    void initTest() {
        tenantFeeConfig = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTenantFeeConfig != null) {
            tenantFeeConfigRepository.delete(insertedTenantFeeConfig);
            insertedTenantFeeConfig = null;
        }
    }

    @Test
    @Transactional
    void createTenantFeeConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);
        var returnedTenantFeeConfigDTO = om.readValue(
            restTenantFeeConfigMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TenantFeeConfigDTO.class
        );

        // Validate the TenantFeeConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTenantFeeConfig = tenantFeeConfigMapper.toEntity(returnedTenantFeeConfigDTO);
        assertTenantFeeConfigUpdatableFieldsEquals(returnedTenantFeeConfig, getPersistedTenantFeeConfig(returnedTenantFeeConfig));

        insertedTenantFeeConfig = returnedTenantFeeConfig;
    }

    @Test
    @Transactional
    void createTenantFeeConfigWithExistingId() throws Exception {
        // Create the TenantFeeConfig with an existing ID
        tenantFeeConfig.setId(1L);
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantFeeConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFixedFeeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantFeeConfig.setFixedFee(null);

        // Create the TenantFeeConfig, which fails.
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        restTenantFeeConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPercentageFeeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantFeeConfig.setPercentageFee(null);

        // Create the TenantFeeConfig, which fails.
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        restTenantFeeConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFeeBearerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantFeeConfig.setFeeBearer(null);

        // Create the TenantFeeConfig, which fails.
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        restTenantFeeConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantFeeConfig.setIsActive(null);

        // Create the TenantFeeConfig, which fails.
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        restTenantFeeConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigs() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantFeeConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].fixedFee").value(hasItem(sameNumber(DEFAULT_FIXED_FEE))))
            .andExpect(jsonPath("$.[*].percentageFee").value(hasItem(sameNumber(DEFAULT_PERCENTAGE_FEE))))
            .andExpect(jsonPath("$.[*].capAmount").value(hasItem(sameNumber(DEFAULT_CAP_AMOUNT))))
            .andExpect(jsonPath("$.[*].feeBearer").value(hasItem(DEFAULT_FEE_BEARER.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getTenantFeeConfig() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get the tenantFeeConfig
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, tenantFeeConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenantFeeConfig.getId().intValue()))
            .andExpect(jsonPath("$.fixedFee").value(sameNumber(DEFAULT_FIXED_FEE)))
            .andExpect(jsonPath("$.percentageFee").value(sameNumber(DEFAULT_PERCENTAGE_FEE)))
            .andExpect(jsonPath("$.capAmount").value(sameNumber(DEFAULT_CAP_AMOUNT)))
            .andExpect(jsonPath("$.feeBearer").value(DEFAULT_FEE_BEARER.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getTenantFeeConfigsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        Long id = tenantFeeConfig.getId();

        defaultTenantFeeConfigFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTenantFeeConfigFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTenantFeeConfigFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee equals to
        defaultTenantFeeConfigFiltering("fixedFee.equals=" + DEFAULT_FIXED_FEE, "fixedFee.equals=" + UPDATED_FIXED_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee in
        defaultTenantFeeConfigFiltering("fixedFee.in=" + DEFAULT_FIXED_FEE + "," + UPDATED_FIXED_FEE, "fixedFee.in=" + UPDATED_FIXED_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee is not null
        defaultTenantFeeConfigFiltering("fixedFee.specified=true", "fixedFee.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee is greater than or equal to
        defaultTenantFeeConfigFiltering(
            "fixedFee.greaterThanOrEqual=" + DEFAULT_FIXED_FEE,
            "fixedFee.greaterThanOrEqual=" + UPDATED_FIXED_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee is less than or equal to
        defaultTenantFeeConfigFiltering("fixedFee.lessThanOrEqual=" + DEFAULT_FIXED_FEE, "fixedFee.lessThanOrEqual=" + SMALLER_FIXED_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee is less than
        defaultTenantFeeConfigFiltering("fixedFee.lessThan=" + UPDATED_FIXED_FEE, "fixedFee.lessThan=" + DEFAULT_FIXED_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFixedFeeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where fixedFee is greater than
        defaultTenantFeeConfigFiltering("fixedFee.greaterThan=" + SMALLER_FIXED_FEE, "fixedFee.greaterThan=" + DEFAULT_FIXED_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee equals to
        defaultTenantFeeConfigFiltering("percentageFee.equals=" + DEFAULT_PERCENTAGE_FEE, "percentageFee.equals=" + UPDATED_PERCENTAGE_FEE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee in
        defaultTenantFeeConfigFiltering(
            "percentageFee.in=" + DEFAULT_PERCENTAGE_FEE + "," + UPDATED_PERCENTAGE_FEE,
            "percentageFee.in=" + UPDATED_PERCENTAGE_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee is not null
        defaultTenantFeeConfigFiltering("percentageFee.specified=true", "percentageFee.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee is greater than or equal to
        defaultTenantFeeConfigFiltering(
            "percentageFee.greaterThanOrEqual=" + DEFAULT_PERCENTAGE_FEE,
            "percentageFee.greaterThanOrEqual=" + UPDATED_PERCENTAGE_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee is less than or equal to
        defaultTenantFeeConfigFiltering(
            "percentageFee.lessThanOrEqual=" + DEFAULT_PERCENTAGE_FEE,
            "percentageFee.lessThanOrEqual=" + SMALLER_PERCENTAGE_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee is less than
        defaultTenantFeeConfigFiltering(
            "percentageFee.lessThan=" + UPDATED_PERCENTAGE_FEE,
            "percentageFee.lessThan=" + DEFAULT_PERCENTAGE_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByPercentageFeeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where percentageFee is greater than
        defaultTenantFeeConfigFiltering(
            "percentageFee.greaterThan=" + SMALLER_PERCENTAGE_FEE,
            "percentageFee.greaterThan=" + DEFAULT_PERCENTAGE_FEE
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount equals to
        defaultTenantFeeConfigFiltering("capAmount.equals=" + DEFAULT_CAP_AMOUNT, "capAmount.equals=" + UPDATED_CAP_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount in
        defaultTenantFeeConfigFiltering(
            "capAmount.in=" + DEFAULT_CAP_AMOUNT + "," + UPDATED_CAP_AMOUNT,
            "capAmount.in=" + UPDATED_CAP_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount is not null
        defaultTenantFeeConfigFiltering("capAmount.specified=true", "capAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount is greater than or equal to
        defaultTenantFeeConfigFiltering(
            "capAmount.greaterThanOrEqual=" + DEFAULT_CAP_AMOUNT,
            "capAmount.greaterThanOrEqual=" + UPDATED_CAP_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount is less than or equal to
        defaultTenantFeeConfigFiltering(
            "capAmount.lessThanOrEqual=" + DEFAULT_CAP_AMOUNT,
            "capAmount.lessThanOrEqual=" + SMALLER_CAP_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount is less than
        defaultTenantFeeConfigFiltering("capAmount.lessThan=" + UPDATED_CAP_AMOUNT, "capAmount.lessThan=" + DEFAULT_CAP_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCapAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where capAmount is greater than
        defaultTenantFeeConfigFiltering("capAmount.greaterThan=" + SMALLER_CAP_AMOUNT, "capAmount.greaterThan=" + DEFAULT_CAP_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFeeBearerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where feeBearer equals to
        defaultTenantFeeConfigFiltering("feeBearer.equals=" + DEFAULT_FEE_BEARER, "feeBearer.equals=" + UPDATED_FEE_BEARER);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFeeBearerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where feeBearer in
        defaultTenantFeeConfigFiltering(
            "feeBearer.in=" + DEFAULT_FEE_BEARER + "," + UPDATED_FEE_BEARER,
            "feeBearer.in=" + UPDATED_FEE_BEARER
        );
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByFeeBearerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where feeBearer is not null
        defaultTenantFeeConfigFiltering("feeBearer.specified=true", "feeBearer.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where isActive equals to
        defaultTenantFeeConfigFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where isActive in
        defaultTenantFeeConfigFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        // Get all the tenantFeeConfigList where isActive is not null
        defaultTenantFeeConfigFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        tenantFeeConfig.setTenant(tenant);
        tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);
        Long tenantId = tenant.getId();
        // Get all the tenantFeeConfigList where tenant equals to tenantId
        defaultTenantFeeConfigShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the tenantFeeConfigList where tenant equals to (tenantId + 1)
        defaultTenantFeeConfigShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    @Test
    @Transactional
    void getAllTenantFeeConfigsByCountryPaymentMethodIsEqualToSomething() throws Exception {
        CountryPaymentMethod countryPaymentMethod;
        if (TestUtil.findAll(em, CountryPaymentMethod.class).isEmpty()) {
            tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);
            countryPaymentMethod = CountryPaymentMethodResourceIT.createEntity(em);
        } else {
            countryPaymentMethod = TestUtil.findAll(em, CountryPaymentMethod.class).getFirst();
        }
        em.persist(countryPaymentMethod);
        em.flush();
        tenantFeeConfig.setCountryPaymentMethod(countryPaymentMethod);
        tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);
        Long countryPaymentMethodId = countryPaymentMethod.getId();
        // Get all the tenantFeeConfigList where countryPaymentMethod equals to countryPaymentMethodId
        defaultTenantFeeConfigShouldBeFound("countryPaymentMethodId.equals=" + countryPaymentMethodId);

        // Get all the tenantFeeConfigList where countryPaymentMethod equals to (countryPaymentMethodId + 1)
        defaultTenantFeeConfigShouldNotBeFound("countryPaymentMethodId.equals=" + (countryPaymentMethodId + 1));
    }

    private void defaultTenantFeeConfigFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTenantFeeConfigShouldBeFound(shouldBeFound);
        defaultTenantFeeConfigShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTenantFeeConfigShouldBeFound(String filter) throws Exception {
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantFeeConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].fixedFee").value(hasItem(sameNumber(DEFAULT_FIXED_FEE))))
            .andExpect(jsonPath("$.[*].percentageFee").value(hasItem(sameNumber(DEFAULT_PERCENTAGE_FEE))))
            .andExpect(jsonPath("$.[*].capAmount").value(hasItem(sameNumber(DEFAULT_CAP_AMOUNT))))
            .andExpect(jsonPath("$.[*].feeBearer").value(hasItem(DEFAULT_FEE_BEARER.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTenantFeeConfigShouldNotBeFound(String filter) throws Exception {
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTenantFeeConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTenantFeeConfig() throws Exception {
        // Get the tenantFeeConfig
        restTenantFeeConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTenantFeeConfig() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantFeeConfig
        TenantFeeConfig updatedTenantFeeConfig = tenantFeeConfigRepository.findById(tenantFeeConfig.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTenantFeeConfig are not directly saved in db
        em.detach(updatedTenantFeeConfig);
        updatedTenantFeeConfig
            .fixedFee(UPDATED_FIXED_FEE)
            .percentageFee(UPDATED_PERCENTAGE_FEE)
            .capAmount(UPDATED_CAP_AMOUNT)
            .feeBearer(UPDATED_FEE_BEARER)
            .isActive(UPDATED_IS_ACTIVE);
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(updatedTenantFeeConfig);

        restTenantFeeConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantFeeConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantFeeConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTenantFeeConfigToMatchAllProperties(updatedTenantFeeConfig);
    }

    @Test
    @Transactional
    void putNonExistingTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantFeeConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantFeeConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantFeeConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantFeeConfigWithPatch() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantFeeConfig using partial update
        TenantFeeConfig partialUpdatedTenantFeeConfig = new TenantFeeConfig();
        partialUpdatedTenantFeeConfig.setId(tenantFeeConfig.getId());

        partialUpdatedTenantFeeConfig.fixedFee(UPDATED_FIXED_FEE).percentageFee(UPDATED_PERCENTAGE_FEE).isActive(UPDATED_IS_ACTIVE);

        restTenantFeeConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantFeeConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantFeeConfig))
            )
            .andExpect(status().isOk());

        // Validate the TenantFeeConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantFeeConfigUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTenantFeeConfig, tenantFeeConfig),
            getPersistedTenantFeeConfig(tenantFeeConfig)
        );
    }

    @Test
    @Transactional
    void fullUpdateTenantFeeConfigWithPatch() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantFeeConfig using partial update
        TenantFeeConfig partialUpdatedTenantFeeConfig = new TenantFeeConfig();
        partialUpdatedTenantFeeConfig.setId(tenantFeeConfig.getId());

        partialUpdatedTenantFeeConfig
            .fixedFee(UPDATED_FIXED_FEE)
            .percentageFee(UPDATED_PERCENTAGE_FEE)
            .capAmount(UPDATED_CAP_AMOUNT)
            .feeBearer(UPDATED_FEE_BEARER)
            .isActive(UPDATED_IS_ACTIVE);

        restTenantFeeConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantFeeConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantFeeConfig))
            )
            .andExpect(status().isOk());

        // Validate the TenantFeeConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantFeeConfigUpdatableFieldsEquals(
            partialUpdatedTenantFeeConfig,
            getPersistedTenantFeeConfig(partialUpdatedTenantFeeConfig)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantFeeConfigDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantFeeConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantFeeConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenantFeeConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantFeeConfig.setId(longCount.incrementAndGet());

        // Create the TenantFeeConfig
        TenantFeeConfigDTO tenantFeeConfigDTO = tenantFeeConfigMapper.toDto(tenantFeeConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantFeeConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tenantFeeConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantFeeConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenantFeeConfig() throws Exception {
        // Initialize the database
        insertedTenantFeeConfig = tenantFeeConfigRepository.saveAndFlush(tenantFeeConfig);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tenantFeeConfig
        restTenantFeeConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenantFeeConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tenantFeeConfigRepository.count();
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

    protected TenantFeeConfig getPersistedTenantFeeConfig(TenantFeeConfig tenantFeeConfig) {
        return tenantFeeConfigRepository.findById(tenantFeeConfig.getId()).orElseThrow();
    }

    protected void assertPersistedTenantFeeConfigToMatchAllProperties(TenantFeeConfig expectedTenantFeeConfig) {
        assertTenantFeeConfigAllPropertiesEquals(expectedTenantFeeConfig, getPersistedTenantFeeConfig(expectedTenantFeeConfig));
    }

    protected void assertPersistedTenantFeeConfigToMatchUpdatableProperties(TenantFeeConfig expectedTenantFeeConfig) {
        assertTenantFeeConfigAllUpdatablePropertiesEquals(expectedTenantFeeConfig, getPersistedTenantFeeConfig(expectedTenantFeeConfig));
    }
}
