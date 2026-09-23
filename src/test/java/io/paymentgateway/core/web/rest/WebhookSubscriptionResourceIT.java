package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.WebhookSubscriptionAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.repository.WebhookSubscriptionRepository;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import io.paymentgateway.core.service.mapper.WebhookSubscriptionMapper;
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
 * Integration tests for the {@link WebhookSubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WebhookSubscriptionResourceIT {

    private static final String DEFAULT_TARGET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SECRET_HASH = "AAAAAAAAAA";
    private static final String UPDATED_SECRET_HASH = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/webhook-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WebhookSubscriptionRepository webhookSubscriptionRepository;

    @Autowired
    private WebhookSubscriptionMapper webhookSubscriptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWebhookSubscriptionMockMvc;

    private WebhookSubscription webhookSubscription;

    private WebhookSubscription insertedWebhookSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookSubscription createEntity(EntityManager em) {
        WebhookSubscription webhookSubscription = new WebhookSubscription()
            .targetUrl(DEFAULT_TARGET_URL)
            .secretHash(DEFAULT_SECRET_HASH)
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
        webhookSubscription.setTenant(corporateTenant);
        return webhookSubscription;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookSubscription createUpdatedEntity(EntityManager em) {
        WebhookSubscription updatedWebhookSubscription = new WebhookSubscription()
            .targetUrl(UPDATED_TARGET_URL)
            .secretHash(UPDATED_SECRET_HASH)
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
        updatedWebhookSubscription.setTenant(corporateTenant);
        return updatedWebhookSubscription;
    }

    @BeforeEach
    void initTest() {
        webhookSubscription = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWebhookSubscription != null) {
            webhookSubscriptionRepository.delete(insertedWebhookSubscription);
            insertedWebhookSubscription = null;
        }
    }

    @Test
    @Transactional
    void createWebhookSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);
        var returnedWebhookSubscriptionDTO = om.readValue(
            restWebhookSubscriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WebhookSubscriptionDTO.class
        );

        // Validate the WebhookSubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWebhookSubscription = webhookSubscriptionMapper.toEntity(returnedWebhookSubscriptionDTO);
        assertWebhookSubscriptionUpdatableFieldsEquals(
            returnedWebhookSubscription,
            getPersistedWebhookSubscription(returnedWebhookSubscription)
        );

        insertedWebhookSubscription = returnedWebhookSubscription;
    }

    @Test
    @Transactional
    void createWebhookSubscriptionWithExistingId() throws Exception {
        // Create the WebhookSubscription with an existing ID
        webhookSubscription.setId(1L);
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWebhookSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTargetUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookSubscription.setTargetUrl(null);

        // Create the WebhookSubscription, which fails.
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        restWebhookSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSecretHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookSubscription.setSecretHash(null);

        // Create the WebhookSubscription, which fails.
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        restWebhookSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookSubscription.setIsActive(null);

        // Create the WebhookSubscription, which fails.
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        restWebhookSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptions() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookSubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secretHash").value(hasItem(DEFAULT_SECRET_HASH)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getWebhookSubscription() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get the webhookSubscription
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, webhookSubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(webhookSubscription.getId().intValue()))
            .andExpect(jsonPath("$.targetUrl").value(DEFAULT_TARGET_URL))
            .andExpect(jsonPath("$.secretHash").value(DEFAULT_SECRET_HASH))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getWebhookSubscriptionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        Long id = webhookSubscription.getId();

        defaultWebhookSubscriptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWebhookSubscriptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWebhookSubscriptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTargetUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where targetUrl equals to
        defaultWebhookSubscriptionFiltering("targetUrl.equals=" + DEFAULT_TARGET_URL, "targetUrl.equals=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTargetUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where targetUrl in
        defaultWebhookSubscriptionFiltering(
            "targetUrl.in=" + DEFAULT_TARGET_URL + "," + UPDATED_TARGET_URL,
            "targetUrl.in=" + UPDATED_TARGET_URL
        );
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTargetUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where targetUrl is not null
        defaultWebhookSubscriptionFiltering("targetUrl.specified=true", "targetUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTargetUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where targetUrl contains
        defaultWebhookSubscriptionFiltering("targetUrl.contains=" + DEFAULT_TARGET_URL, "targetUrl.contains=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTargetUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where targetUrl does not contain
        defaultWebhookSubscriptionFiltering(
            "targetUrl.doesNotContain=" + UPDATED_TARGET_URL,
            "targetUrl.doesNotContain=" + DEFAULT_TARGET_URL
        );
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsBySecretHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where secretHash equals to
        defaultWebhookSubscriptionFiltering("secretHash.equals=" + DEFAULT_SECRET_HASH, "secretHash.equals=" + UPDATED_SECRET_HASH);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsBySecretHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where secretHash in
        defaultWebhookSubscriptionFiltering(
            "secretHash.in=" + DEFAULT_SECRET_HASH + "," + UPDATED_SECRET_HASH,
            "secretHash.in=" + UPDATED_SECRET_HASH
        );
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsBySecretHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where secretHash is not null
        defaultWebhookSubscriptionFiltering("secretHash.specified=true", "secretHash.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsBySecretHashContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where secretHash contains
        defaultWebhookSubscriptionFiltering("secretHash.contains=" + DEFAULT_SECRET_HASH, "secretHash.contains=" + UPDATED_SECRET_HASH);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsBySecretHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where secretHash does not contain
        defaultWebhookSubscriptionFiltering(
            "secretHash.doesNotContain=" + UPDATED_SECRET_HASH,
            "secretHash.doesNotContain=" + DEFAULT_SECRET_HASH
        );
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where isActive equals to
        defaultWebhookSubscriptionFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where isActive in
        defaultWebhookSubscriptionFiltering(
            "isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE,
            "isActive.in=" + UPDATED_IS_ACTIVE
        );
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        // Get all the webhookSubscriptionList where isActive is not null
        defaultWebhookSubscriptionFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookSubscriptionsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            webhookSubscriptionRepository.saveAndFlush(webhookSubscription);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        webhookSubscription.setTenant(tenant);
        webhookSubscriptionRepository.saveAndFlush(webhookSubscription);
        Long tenantId = tenant.getId();
        // Get all the webhookSubscriptionList where tenant equals to tenantId
        defaultWebhookSubscriptionShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the webhookSubscriptionList where tenant equals to (tenantId + 1)
        defaultWebhookSubscriptionShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultWebhookSubscriptionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWebhookSubscriptionShouldBeFound(shouldBeFound);
        defaultWebhookSubscriptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWebhookSubscriptionShouldBeFound(String filter) throws Exception {
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookSubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secretHash").value(hasItem(DEFAULT_SECRET_HASH)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWebhookSubscriptionShouldNotBeFound(String filter) throws Exception {
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWebhookSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWebhookSubscription() throws Exception {
        // Get the webhookSubscription
        restWebhookSubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWebhookSubscription() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookSubscription
        WebhookSubscription updatedWebhookSubscription = webhookSubscriptionRepository.findById(webhookSubscription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWebhookSubscription are not directly saved in db
        em.detach(updatedWebhookSubscription);
        updatedWebhookSubscription.targetUrl(UPDATED_TARGET_URL).secretHash(UPDATED_SECRET_HASH).isActive(UPDATED_IS_ACTIVE);
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(updatedWebhookSubscription);

        restWebhookSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookSubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWebhookSubscriptionToMatchAllProperties(updatedWebhookSubscription);
    }

    @Test
    @Transactional
    void putNonExistingWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookSubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookSubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWebhookSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookSubscription using partial update
        WebhookSubscription partialUpdatedWebhookSubscription = new WebhookSubscription();
        partialUpdatedWebhookSubscription.setId(webhookSubscription.getId());

        partialUpdatedWebhookSubscription.isActive(UPDATED_IS_ACTIVE);

        restWebhookSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookSubscription))
            )
            .andExpect(status().isOk());

        // Validate the WebhookSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWebhookSubscription, webhookSubscription),
            getPersistedWebhookSubscription(webhookSubscription)
        );
    }

    @Test
    @Transactional
    void fullUpdateWebhookSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookSubscription using partial update
        WebhookSubscription partialUpdatedWebhookSubscription = new WebhookSubscription();
        partialUpdatedWebhookSubscription.setId(webhookSubscription.getId());

        partialUpdatedWebhookSubscription.targetUrl(UPDATED_TARGET_URL).secretHash(UPDATED_SECRET_HASH).isActive(UPDATED_IS_ACTIVE);

        restWebhookSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookSubscription))
            )
            .andExpect(status().isOk());

        // Validate the WebhookSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookSubscriptionUpdatableFieldsEquals(
            partialUpdatedWebhookSubscription,
            getPersistedWebhookSubscription(partialUpdatedWebhookSubscription)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, webhookSubscriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWebhookSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookSubscription.setId(longCount.incrementAndGet());

        // Create the WebhookSubscription
        WebhookSubscriptionDTO webhookSubscriptionDTO = webhookSubscriptionMapper.toDto(webhookSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(webhookSubscriptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWebhookSubscription() throws Exception {
        // Initialize the database
        insertedWebhookSubscription = webhookSubscriptionRepository.saveAndFlush(webhookSubscription);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the webhookSubscription
        restWebhookSubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, webhookSubscription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return webhookSubscriptionRepository.count();
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

    protected WebhookSubscription getPersistedWebhookSubscription(WebhookSubscription webhookSubscription) {
        return webhookSubscriptionRepository.findById(webhookSubscription.getId()).orElseThrow();
    }

    protected void assertPersistedWebhookSubscriptionToMatchAllProperties(WebhookSubscription expectedWebhookSubscription) {
        assertWebhookSubscriptionAllPropertiesEquals(
            expectedWebhookSubscription,
            getPersistedWebhookSubscription(expectedWebhookSubscription)
        );
    }

    protected void assertPersistedWebhookSubscriptionToMatchUpdatableProperties(WebhookSubscription expectedWebhookSubscription) {
        assertWebhookSubscriptionAllUpdatablePropertiesEquals(
            expectedWebhookSubscription,
            getPersistedWebhookSubscription(expectedWebhookSubscription)
        );
    }
}
