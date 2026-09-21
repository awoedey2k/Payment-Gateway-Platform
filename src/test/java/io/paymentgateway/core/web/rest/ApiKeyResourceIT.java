package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.ApiKeyAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.repository.ApiKeyRepository;
import io.paymentgateway.core.service.dto.ApiKeyDTO;
import io.paymentgateway.core.service.mapper.ApiKeyMapper;
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
 * Integration tests for the {@link ApiKeyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApiKeyResourceIT {

    private static final String DEFAULT_KEY_PREFIX = "AAAAAAAAAA";
    private static final String UPDATED_KEY_PREFIX = "BBBBBBBBBB";

    private static final String DEFAULT_KEY_HASH = "AAAAAAAAAA";
    private static final String UPDATED_KEY_HASH = "BBBBBBBBBB";

    private static final ApiEnvironment DEFAULT_ENVIRONMENT = ApiEnvironment.TEST;
    private static final ApiEnvironment UPDATED_ENVIRONMENT = ApiEnvironment.LIVE;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_ISSUED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_REVOKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVOKED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_GRACE_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_GRACE_EXPIRES_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/api-keys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApiKeyMockMvc;

    private ApiKey apiKey;

    private ApiKey insertedApiKey;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createEntity(EntityManager em) {
        ApiKey apiKey = new ApiKey()
            .keyPrefix(DEFAULT_KEY_PREFIX)
            .keyHash(DEFAULT_KEY_HASH)
            .environment(DEFAULT_ENVIRONMENT)
            .isActive(DEFAULT_IS_ACTIVE)
            .issuedAt(DEFAULT_ISSUED_AT)
            .revokedAt(DEFAULT_REVOKED_AT)
            .graceExpiresAt(DEFAULT_GRACE_EXPIRES_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        apiKey.setTenant(corporateTenant);
        return apiKey;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createUpdatedEntity(EntityManager em) {
        ApiKey updatedApiKey = new ApiKey()
            .keyPrefix(UPDATED_KEY_PREFIX)
            .keyHash(UPDATED_KEY_HASH)
            .environment(UPDATED_ENVIRONMENT)
            .isActive(UPDATED_IS_ACTIVE)
            .issuedAt(UPDATED_ISSUED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .graceExpiresAt(UPDATED_GRACE_EXPIRES_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedApiKey.setTenant(corporateTenant);
        return updatedApiKey;
    }

    @BeforeEach
    void initTest() {
        apiKey = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedApiKey != null) {
            apiKeyRepository.delete(insertedApiKey);
            insertedApiKey = null;
        }
    }

    @Test
    @Transactional
    void createApiKey() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);
        var returnedApiKeyDTO = om.readValue(
            restApiKeyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApiKeyDTO.class
        );

        // Validate the ApiKey in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApiKey = apiKeyMapper.toEntity(returnedApiKeyDTO);
        assertApiKeyUpdatableFieldsEquals(returnedApiKey, getPersistedApiKey(returnedApiKey));

        insertedApiKey = returnedApiKey;
    }

    @Test
    @Transactional
    void createApiKeyWithExistingId() throws Exception {
        // Create the ApiKey with an existing ID
        apiKey.setId(1L);
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyPrefixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiKey.setKeyPrefix(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkKeyHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiKey.setKeyHash(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnvironmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiKey.setEnvironment(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiKey.setIsActive(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiKey.setIssuedAt(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApiKeys() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apiKey.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyPrefix").value(hasItem(DEFAULT_KEY_PREFIX)))
            .andExpect(jsonPath("$.[*].keyHash").value(hasItem(DEFAULT_KEY_HASH)))
            .andExpect(jsonPath("$.[*].environment").value(hasItem(DEFAULT_ENVIRONMENT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].revokedAt").value(hasItem(DEFAULT_REVOKED_AT.toString())))
            .andExpect(jsonPath("$.[*].graceExpiresAt").value(hasItem(DEFAULT_GRACE_EXPIRES_AT.toString())));
    }

    @Test
    @Transactional
    void getApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get the apiKey
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL_ID, apiKey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(apiKey.getId().intValue()))
            .andExpect(jsonPath("$.keyPrefix").value(DEFAULT_KEY_PREFIX))
            .andExpect(jsonPath("$.keyHash").value(DEFAULT_KEY_HASH))
            .andExpect(jsonPath("$.environment").value(DEFAULT_ENVIRONMENT.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.issuedAt").value(DEFAULT_ISSUED_AT.toString()))
            .andExpect(jsonPath("$.revokedAt").value(DEFAULT_REVOKED_AT.toString()))
            .andExpect(jsonPath("$.graceExpiresAt").value(DEFAULT_GRACE_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getApiKeysByIdFiltering() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        Long id = apiKey.getId();

        defaultApiKeyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultApiKeyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultApiKeyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyPrefixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyPrefix equals to
        defaultApiKeyFiltering("keyPrefix.equals=" + DEFAULT_KEY_PREFIX, "keyPrefix.equals=" + UPDATED_KEY_PREFIX);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyPrefixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyPrefix in
        defaultApiKeyFiltering("keyPrefix.in=" + DEFAULT_KEY_PREFIX + "," + UPDATED_KEY_PREFIX, "keyPrefix.in=" + UPDATED_KEY_PREFIX);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyPrefixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyPrefix is not null
        defaultApiKeyFiltering("keyPrefix.specified=true", "keyPrefix.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyPrefixContainsSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyPrefix contains
        defaultApiKeyFiltering("keyPrefix.contains=" + DEFAULT_KEY_PREFIX, "keyPrefix.contains=" + UPDATED_KEY_PREFIX);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyPrefixNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyPrefix does not contain
        defaultApiKeyFiltering("keyPrefix.doesNotContain=" + UPDATED_KEY_PREFIX, "keyPrefix.doesNotContain=" + DEFAULT_KEY_PREFIX);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyHash equals to
        defaultApiKeyFiltering("keyHash.equals=" + DEFAULT_KEY_HASH, "keyHash.equals=" + UPDATED_KEY_HASH);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyHash in
        defaultApiKeyFiltering("keyHash.in=" + DEFAULT_KEY_HASH + "," + UPDATED_KEY_HASH, "keyHash.in=" + UPDATED_KEY_HASH);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyHash is not null
        defaultApiKeyFiltering("keyHash.specified=true", "keyHash.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyHashContainsSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyHash contains
        defaultApiKeyFiltering("keyHash.contains=" + DEFAULT_KEY_HASH, "keyHash.contains=" + UPDATED_KEY_HASH);
    }

    @Test
    @Transactional
    void getAllApiKeysByKeyHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where keyHash does not contain
        defaultApiKeyFiltering("keyHash.doesNotContain=" + UPDATED_KEY_HASH, "keyHash.doesNotContain=" + DEFAULT_KEY_HASH);
    }

    @Test
    @Transactional
    void getAllApiKeysByEnvironmentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where environment equals to
        defaultApiKeyFiltering("environment.equals=" + DEFAULT_ENVIRONMENT, "environment.equals=" + UPDATED_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllApiKeysByEnvironmentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where environment in
        defaultApiKeyFiltering(
            "environment.in=" + DEFAULT_ENVIRONMENT + "," + UPDATED_ENVIRONMENT,
            "environment.in=" + UPDATED_ENVIRONMENT
        );
    }

    @Test
    @Transactional
    void getAllApiKeysByEnvironmentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where environment is not null
        defaultApiKeyFiltering("environment.specified=true", "environment.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where isActive equals to
        defaultApiKeyFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllApiKeysByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where isActive in
        defaultApiKeyFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllApiKeysByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where isActive is not null
        defaultApiKeyFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByIssuedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where issuedAt equals to
        defaultApiKeyFiltering("issuedAt.equals=" + DEFAULT_ISSUED_AT, "issuedAt.equals=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllApiKeysByIssuedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where issuedAt in
        defaultApiKeyFiltering("issuedAt.in=" + DEFAULT_ISSUED_AT + "," + UPDATED_ISSUED_AT, "issuedAt.in=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllApiKeysByIssuedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where issuedAt is not null
        defaultApiKeyFiltering("issuedAt.specified=true", "issuedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByRevokedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where revokedAt equals to
        defaultApiKeyFiltering("revokedAt.equals=" + DEFAULT_REVOKED_AT, "revokedAt.equals=" + UPDATED_REVOKED_AT);
    }

    @Test
    @Transactional
    void getAllApiKeysByRevokedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where revokedAt in
        defaultApiKeyFiltering("revokedAt.in=" + DEFAULT_REVOKED_AT + "," + UPDATED_REVOKED_AT, "revokedAt.in=" + UPDATED_REVOKED_AT);
    }

    @Test
    @Transactional
    void getAllApiKeysByRevokedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where revokedAt is not null
        defaultApiKeyFiltering("revokedAt.specified=true", "revokedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByGraceExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where graceExpiresAt equals to
        defaultApiKeyFiltering("graceExpiresAt.equals=" + DEFAULT_GRACE_EXPIRES_AT, "graceExpiresAt.equals=" + UPDATED_GRACE_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllApiKeysByGraceExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where graceExpiresAt in
        defaultApiKeyFiltering(
            "graceExpiresAt.in=" + DEFAULT_GRACE_EXPIRES_AT + "," + UPDATED_GRACE_EXPIRES_AT,
            "graceExpiresAt.in=" + UPDATED_GRACE_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllApiKeysByGraceExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList where graceExpiresAt is not null
        defaultApiKeyFiltering("graceExpiresAt.specified=true", "graceExpiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllApiKeysByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            apiKeyRepository.saveAndFlush(apiKey);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        apiKey.setTenant(tenant);
        apiKeyRepository.saveAndFlush(apiKey);
        Long tenantId = tenant.getId();
        // Get all the apiKeyList where tenant equals to tenantId
        defaultApiKeyShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the apiKeyList where tenant equals to (tenantId + 1)
        defaultApiKeyShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultApiKeyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultApiKeyShouldBeFound(shouldBeFound);
        defaultApiKeyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultApiKeyShouldBeFound(String filter) throws Exception {
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apiKey.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyPrefix").value(hasItem(DEFAULT_KEY_PREFIX)))
            .andExpect(jsonPath("$.[*].keyHash").value(hasItem(DEFAULT_KEY_HASH)))
            .andExpect(jsonPath("$.[*].environment").value(hasItem(DEFAULT_ENVIRONMENT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].revokedAt").value(hasItem(DEFAULT_REVOKED_AT.toString())))
            .andExpect(jsonPath("$.[*].graceExpiresAt").value(hasItem(DEFAULT_GRACE_EXPIRES_AT.toString())));

        // Check, that the count call also returns 1
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultApiKeyShouldNotBeFound(String filter) throws Exception {
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingApiKey() throws Exception {
        // Get the apiKey
        restApiKeyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiKey
        ApiKey updatedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApiKey are not directly saved in db
        em.detach(updatedApiKey);
        updatedApiKey
            .keyPrefix(UPDATED_KEY_PREFIX)
            .keyHash(UPDATED_KEY_HASH)
            .environment(UPDATED_ENVIRONMENT)
            .isActive(UPDATED_IS_ACTIVE)
            .issuedAt(UPDATED_ISSUED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .graceExpiresAt(UPDATED_GRACE_EXPIRES_AT);
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(updatedApiKey);

        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiKeyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApiKeyToMatchAllProperties(updatedApiKey);
    }

    @Test
    @Transactional
    void putNonExistingApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiKeyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        partialUpdatedApiKey
            .keyPrefix(UPDATED_KEY_PREFIX)
            .keyHash(UPDATED_KEY_HASH)
            .environment(UPDATED_ENVIRONMENT)
            .graceExpiresAt(UPDATED_GRACE_EXPIRES_AT);

        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiKey))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiKeyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedApiKey, apiKey), getPersistedApiKey(apiKey));
    }

    @Test
    @Transactional
    void fullUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        partialUpdatedApiKey
            .keyPrefix(UPDATED_KEY_PREFIX)
            .keyHash(UPDATED_KEY_HASH)
            .environment(UPDATED_ENVIRONMENT)
            .isActive(UPDATED_IS_ACTIVE)
            .issuedAt(UPDATED_ISSUED_AT)
            .revokedAt(UPDATED_REVOKED_AT)
            .graceExpiresAt(UPDATED_GRACE_EXPIRES_AT);

        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiKey))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiKeyUpdatableFieldsEquals(partialUpdatedApiKey, getPersistedApiKey(partialUpdatedApiKey));
    }

    @Test
    @Transactional
    void patchNonExistingApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, apiKeyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the apiKey
        restApiKeyMockMvc
            .perform(delete(ENTITY_API_URL_ID, apiKey.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return apiKeyRepository.count();
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

    protected ApiKey getPersistedApiKey(ApiKey apiKey) {
        return apiKeyRepository.findById(apiKey.getId()).orElseThrow();
    }

    protected void assertPersistedApiKeyToMatchAllProperties(ApiKey expectedApiKey) {
        assertApiKeyAllPropertiesEquals(expectedApiKey, getPersistedApiKey(expectedApiKey));
    }

    protected void assertPersistedApiKeyToMatchUpdatableProperties(ApiKey expectedApiKey) {
        assertApiKeyAllUpdatablePropertiesEquals(expectedApiKey, getPersistedApiKey(expectedApiKey));
    }
}
