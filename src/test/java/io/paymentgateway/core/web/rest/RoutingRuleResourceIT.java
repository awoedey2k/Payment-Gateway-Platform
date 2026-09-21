package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.RoutingRuleAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.RoutingRule;
import io.paymentgateway.core.domain.enumeration.RoutingRuleScope;
import io.paymentgateway.core.repository.RoutingRuleRepository;
import io.paymentgateway.core.service.dto.RoutingRuleDTO;
import io.paymentgateway.core.service.mapper.RoutingRuleMapper;
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
 * Integration tests for the {@link RoutingRuleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoutingRuleResourceIT {

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;
    private static final Integer SMALLER_PRIORITY = 1 - 1;

    private static final RoutingRuleScope DEFAULT_SCOPE = RoutingRuleScope.TENANT_CUSTOM;
    private static final RoutingRuleScope UPDATED_SCOPE = RoutingRuleScope.PLATFORM_GLOBAL;

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_CARD_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMARY_ADAPTER = "AAAAAAAAAA";
    private static final String UPDATED_PRIMARY_ADAPTER = "BBBBBBBBBB";

    private static final String DEFAULT_FALLBACK_ADAPTER = "AAAAAAAAAA";
    private static final String UPDATED_FALLBACK_ADAPTER = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_RETRIES = 1;
    private static final Integer UPDATED_MAX_RETRIES = 2;
    private static final Integer SMALLER_MAX_RETRIES = 1 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/routing-rules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoutingRuleRepository routingRuleRepository;

    @Autowired
    private RoutingRuleMapper routingRuleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoutingRuleMockMvc;

    private RoutingRule routingRule;

    private RoutingRule insertedRoutingRule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoutingRule createEntity() {
        return new RoutingRule()
            .priority(DEFAULT_PRIORITY)
            .scope(DEFAULT_SCOPE)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .cardBrand(DEFAULT_CARD_BRAND)
            .primaryAdapter(DEFAULT_PRIMARY_ADAPTER)
            .fallbackAdapter(DEFAULT_FALLBACK_ADAPTER)
            .maxRetries(DEFAULT_MAX_RETRIES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoutingRule createUpdatedEntity() {
        return new RoutingRule()
            .priority(UPDATED_PRIORITY)
            .scope(UPDATED_SCOPE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .cardBrand(UPDATED_CARD_BRAND)
            .primaryAdapter(UPDATED_PRIMARY_ADAPTER)
            .fallbackAdapter(UPDATED_FALLBACK_ADAPTER)
            .maxRetries(UPDATED_MAX_RETRIES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        routingRule = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRoutingRule != null) {
            routingRuleRepository.delete(insertedRoutingRule);
            insertedRoutingRule = null;
        }
    }

    @Test
    @Transactional
    void createRoutingRule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);
        var returnedRoutingRuleDTO = om.readValue(
            restRoutingRuleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RoutingRuleDTO.class
        );

        // Validate the RoutingRule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRoutingRule = routingRuleMapper.toEntity(returnedRoutingRuleDTO);
        assertRoutingRuleUpdatableFieldsEquals(returnedRoutingRule, getPersistedRoutingRule(returnedRoutingRule));

        insertedRoutingRule = returnedRoutingRule;
    }

    @Test
    @Transactional
    void createRoutingRuleWithExistingId() throws Exception {
        // Create the RoutingRule with an existing ID
        routingRule.setId(1L);
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routingRule.setPriority(null);

        // Create the RoutingRule, which fails.
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routingRule.setScope(null);

        // Create the RoutingRule, which fails.
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrimaryAdapterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routingRule.setPrimaryAdapter(null);

        // Create the RoutingRule, which fails.
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxRetriesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routingRule.setMaxRetries(null);

        // Create the RoutingRule, which fails.
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routingRule.setIsActive(null);

        // Create the RoutingRule, which fails.
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        restRoutingRuleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoutingRules() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(routingRule.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].scope").value(hasItem(DEFAULT_SCOPE.toString())))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].cardBrand").value(hasItem(DEFAULT_CARD_BRAND)))
            .andExpect(jsonPath("$.[*].primaryAdapter").value(hasItem(DEFAULT_PRIMARY_ADAPTER)))
            .andExpect(jsonPath("$.[*].fallbackAdapter").value(hasItem(DEFAULT_FALLBACK_ADAPTER)))
            .andExpect(jsonPath("$.[*].maxRetries").value(hasItem(DEFAULT_MAX_RETRIES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getRoutingRule() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get the routingRule
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL_ID, routingRule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(routingRule.getId().intValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
            .andExpect(jsonPath("$.scope").value(DEFAULT_SCOPE.toString()))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.cardBrand").value(DEFAULT_CARD_BRAND))
            .andExpect(jsonPath("$.primaryAdapter").value(DEFAULT_PRIMARY_ADAPTER))
            .andExpect(jsonPath("$.fallbackAdapter").value(DEFAULT_FALLBACK_ADAPTER))
            .andExpect(jsonPath("$.maxRetries").value(DEFAULT_MAX_RETRIES))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getRoutingRulesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        Long id = routingRule.getId();

        defaultRoutingRuleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRoutingRuleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRoutingRuleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority equals to
        defaultRoutingRuleFiltering("priority.equals=" + DEFAULT_PRIORITY, "priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority in
        defaultRoutingRuleFiltering("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY, "priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority is not null
        defaultRoutingRuleFiltering("priority.specified=true", "priority.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority is greater than or equal to
        defaultRoutingRuleFiltering("priority.greaterThanOrEqual=" + DEFAULT_PRIORITY, "priority.greaterThanOrEqual=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority is less than or equal to
        defaultRoutingRuleFiltering("priority.lessThanOrEqual=" + DEFAULT_PRIORITY, "priority.lessThanOrEqual=" + SMALLER_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority is less than
        defaultRoutingRuleFiltering("priority.lessThan=" + UPDATED_PRIORITY, "priority.lessThan=" + DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPriorityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where priority is greater than
        defaultRoutingRuleFiltering("priority.greaterThan=" + SMALLER_PRIORITY, "priority.greaterThan=" + DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByScopeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where scope equals to
        defaultRoutingRuleFiltering("scope.equals=" + DEFAULT_SCOPE, "scope.equals=" + UPDATED_SCOPE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByScopeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where scope in
        defaultRoutingRuleFiltering("scope.in=" + DEFAULT_SCOPE + "," + UPDATED_SCOPE, "scope.in=" + UPDATED_SCOPE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByScopeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where scope is not null
        defaultRoutingRuleFiltering("scope.specified=true", "scope.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where countryCode equals to
        defaultRoutingRuleFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where countryCode in
        defaultRoutingRuleFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where countryCode is not null
        defaultRoutingRuleFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where countryCode contains
        defaultRoutingRuleFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where countryCode does not contain
        defaultRoutingRuleFiltering(
            "countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE,
            "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where currencyCode equals to
        defaultRoutingRuleFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where currencyCode in
        defaultRoutingRuleFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where currencyCode is not null
        defaultRoutingRuleFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where currencyCode contains
        defaultRoutingRuleFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where currencyCode does not contain
        defaultRoutingRuleFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCardBrandIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where cardBrand equals to
        defaultRoutingRuleFiltering("cardBrand.equals=" + DEFAULT_CARD_BRAND, "cardBrand.equals=" + UPDATED_CARD_BRAND);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCardBrandIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where cardBrand in
        defaultRoutingRuleFiltering("cardBrand.in=" + DEFAULT_CARD_BRAND + "," + UPDATED_CARD_BRAND, "cardBrand.in=" + UPDATED_CARD_BRAND);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCardBrandIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where cardBrand is not null
        defaultRoutingRuleFiltering("cardBrand.specified=true", "cardBrand.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCardBrandContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where cardBrand contains
        defaultRoutingRuleFiltering("cardBrand.contains=" + DEFAULT_CARD_BRAND, "cardBrand.contains=" + UPDATED_CARD_BRAND);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByCardBrandNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where cardBrand does not contain
        defaultRoutingRuleFiltering("cardBrand.doesNotContain=" + UPDATED_CARD_BRAND, "cardBrand.doesNotContain=" + DEFAULT_CARD_BRAND);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPrimaryAdapterIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where primaryAdapter equals to
        defaultRoutingRuleFiltering("primaryAdapter.equals=" + DEFAULT_PRIMARY_ADAPTER, "primaryAdapter.equals=" + UPDATED_PRIMARY_ADAPTER);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPrimaryAdapterIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where primaryAdapter in
        defaultRoutingRuleFiltering(
            "primaryAdapter.in=" + DEFAULT_PRIMARY_ADAPTER + "," + UPDATED_PRIMARY_ADAPTER,
            "primaryAdapter.in=" + UPDATED_PRIMARY_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPrimaryAdapterIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where primaryAdapter is not null
        defaultRoutingRuleFiltering("primaryAdapter.specified=true", "primaryAdapter.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPrimaryAdapterContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where primaryAdapter contains
        defaultRoutingRuleFiltering(
            "primaryAdapter.contains=" + DEFAULT_PRIMARY_ADAPTER,
            "primaryAdapter.contains=" + UPDATED_PRIMARY_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByPrimaryAdapterNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where primaryAdapter does not contain
        defaultRoutingRuleFiltering(
            "primaryAdapter.doesNotContain=" + UPDATED_PRIMARY_ADAPTER,
            "primaryAdapter.doesNotContain=" + DEFAULT_PRIMARY_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByFallbackAdapterIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where fallbackAdapter equals to
        defaultRoutingRuleFiltering(
            "fallbackAdapter.equals=" + DEFAULT_FALLBACK_ADAPTER,
            "fallbackAdapter.equals=" + UPDATED_FALLBACK_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByFallbackAdapterIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where fallbackAdapter in
        defaultRoutingRuleFiltering(
            "fallbackAdapter.in=" + DEFAULT_FALLBACK_ADAPTER + "," + UPDATED_FALLBACK_ADAPTER,
            "fallbackAdapter.in=" + UPDATED_FALLBACK_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByFallbackAdapterIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where fallbackAdapter is not null
        defaultRoutingRuleFiltering("fallbackAdapter.specified=true", "fallbackAdapter.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByFallbackAdapterContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where fallbackAdapter contains
        defaultRoutingRuleFiltering(
            "fallbackAdapter.contains=" + DEFAULT_FALLBACK_ADAPTER,
            "fallbackAdapter.contains=" + UPDATED_FALLBACK_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByFallbackAdapterNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where fallbackAdapter does not contain
        defaultRoutingRuleFiltering(
            "fallbackAdapter.doesNotContain=" + UPDATED_FALLBACK_ADAPTER,
            "fallbackAdapter.doesNotContain=" + DEFAULT_FALLBACK_ADAPTER
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries equals to
        defaultRoutingRuleFiltering("maxRetries.equals=" + DEFAULT_MAX_RETRIES, "maxRetries.equals=" + UPDATED_MAX_RETRIES);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries in
        defaultRoutingRuleFiltering(
            "maxRetries.in=" + DEFAULT_MAX_RETRIES + "," + UPDATED_MAX_RETRIES,
            "maxRetries.in=" + UPDATED_MAX_RETRIES
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries is not null
        defaultRoutingRuleFiltering("maxRetries.specified=true", "maxRetries.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries is greater than or equal to
        defaultRoutingRuleFiltering(
            "maxRetries.greaterThanOrEqual=" + DEFAULT_MAX_RETRIES,
            "maxRetries.greaterThanOrEqual=" + UPDATED_MAX_RETRIES
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries is less than or equal to
        defaultRoutingRuleFiltering(
            "maxRetries.lessThanOrEqual=" + DEFAULT_MAX_RETRIES,
            "maxRetries.lessThanOrEqual=" + SMALLER_MAX_RETRIES
        );
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries is less than
        defaultRoutingRuleFiltering("maxRetries.lessThan=" + UPDATED_MAX_RETRIES, "maxRetries.lessThan=" + DEFAULT_MAX_RETRIES);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByMaxRetriesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where maxRetries is greater than
        defaultRoutingRuleFiltering("maxRetries.greaterThan=" + SMALLER_MAX_RETRIES, "maxRetries.greaterThan=" + DEFAULT_MAX_RETRIES);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where isActive equals to
        defaultRoutingRuleFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where isActive in
        defaultRoutingRuleFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRoutingRulesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        // Get all the routingRuleList where isActive is not null
        defaultRoutingRuleFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllRoutingRulesByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            routingRuleRepository.saveAndFlush(routingRule);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        routingRule.setTenant(tenant);
        routingRuleRepository.saveAndFlush(routingRule);
        Long tenantId = tenant.getId();
        // Get all the routingRuleList where tenant equals to tenantId
        defaultRoutingRuleShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the routingRuleList where tenant equals to (tenantId + 1)
        defaultRoutingRuleShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultRoutingRuleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRoutingRuleShouldBeFound(shouldBeFound);
        defaultRoutingRuleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRoutingRuleShouldBeFound(String filter) throws Exception {
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(routingRule.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].scope").value(hasItem(DEFAULT_SCOPE.toString())))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].cardBrand").value(hasItem(DEFAULT_CARD_BRAND)))
            .andExpect(jsonPath("$.[*].primaryAdapter").value(hasItem(DEFAULT_PRIMARY_ADAPTER)))
            .andExpect(jsonPath("$.[*].fallbackAdapter").value(hasItem(DEFAULT_FALLBACK_ADAPTER)))
            .andExpect(jsonPath("$.[*].maxRetries").value(hasItem(DEFAULT_MAX_RETRIES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRoutingRuleShouldNotBeFound(String filter) throws Exception {
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRoutingRuleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRoutingRule() throws Exception {
        // Get the routingRule
        restRoutingRuleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoutingRule() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routingRule
        RoutingRule updatedRoutingRule = routingRuleRepository.findById(routingRule.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRoutingRule are not directly saved in db
        em.detach(updatedRoutingRule);
        updatedRoutingRule
            .priority(UPDATED_PRIORITY)
            .scope(UPDATED_SCOPE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .cardBrand(UPDATED_CARD_BRAND)
            .primaryAdapter(UPDATED_PRIMARY_ADAPTER)
            .fallbackAdapter(UPDATED_FALLBACK_ADAPTER)
            .maxRetries(UPDATED_MAX_RETRIES)
            .isActive(UPDATED_IS_ACTIVE);
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(updatedRoutingRule);

        restRoutingRuleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, routingRuleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(routingRuleDTO))
            )
            .andExpect(status().isOk());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRoutingRuleToMatchAllProperties(updatedRoutingRule);
    }

    @Test
    @Transactional
    void putNonExistingRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, routingRuleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(routingRuleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(routingRuleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoutingRuleWithPatch() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routingRule using partial update
        RoutingRule partialUpdatedRoutingRule = new RoutingRule();
        partialUpdatedRoutingRule.setId(routingRule.getId());

        partialUpdatedRoutingRule
            .priority(UPDATED_PRIORITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .cardBrand(UPDATED_CARD_BRAND)
            .maxRetries(UPDATED_MAX_RETRIES)
            .isActive(UPDATED_IS_ACTIVE);

        restRoutingRuleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoutingRule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoutingRule))
            )
            .andExpect(status().isOk());

        // Validate the RoutingRule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoutingRuleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRoutingRule, routingRule),
            getPersistedRoutingRule(routingRule)
        );
    }

    @Test
    @Transactional
    void fullUpdateRoutingRuleWithPatch() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routingRule using partial update
        RoutingRule partialUpdatedRoutingRule = new RoutingRule();
        partialUpdatedRoutingRule.setId(routingRule.getId());

        partialUpdatedRoutingRule
            .priority(UPDATED_PRIORITY)
            .scope(UPDATED_SCOPE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .cardBrand(UPDATED_CARD_BRAND)
            .primaryAdapter(UPDATED_PRIMARY_ADAPTER)
            .fallbackAdapter(UPDATED_FALLBACK_ADAPTER)
            .maxRetries(UPDATED_MAX_RETRIES)
            .isActive(UPDATED_IS_ACTIVE);

        restRoutingRuleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoutingRule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoutingRule))
            )
            .andExpect(status().isOk());

        // Validate the RoutingRule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoutingRuleUpdatableFieldsEquals(partialUpdatedRoutingRule, getPersistedRoutingRule(partialUpdatedRoutingRule));
    }

    @Test
    @Transactional
    void patchNonExistingRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, routingRuleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(routingRuleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(routingRuleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoutingRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routingRule.setId(longCount.incrementAndGet());

        // Create the RoutingRule
        RoutingRuleDTO routingRuleDTO = routingRuleMapper.toDto(routingRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoutingRuleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(routingRuleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoutingRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoutingRule() throws Exception {
        // Initialize the database
        insertedRoutingRule = routingRuleRepository.saveAndFlush(routingRule);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the routingRule
        restRoutingRuleMockMvc
            .perform(delete(ENTITY_API_URL_ID, routingRule.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return routingRuleRepository.count();
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

    protected RoutingRule getPersistedRoutingRule(RoutingRule routingRule) {
        return routingRuleRepository.findById(routingRule.getId()).orElseThrow();
    }

    protected void assertPersistedRoutingRuleToMatchAllProperties(RoutingRule expectedRoutingRule) {
        assertRoutingRuleAllPropertiesEquals(expectedRoutingRule, getPersistedRoutingRule(expectedRoutingRule));
    }

    protected void assertPersistedRoutingRuleToMatchUpdatableProperties(RoutingRule expectedRoutingRule) {
        assertRoutingRuleAllUpdatablePropertiesEquals(expectedRoutingRule, getPersistedRoutingRule(expectedRoutingRule));
    }
}
