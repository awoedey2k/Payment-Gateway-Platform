package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.WebhookDeliveryAttemptAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.WebhookDeliveryAttempt;
import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.domain.enumeration.WebhookDeliveryStatus;
import io.paymentgateway.core.repository.WebhookDeliveryAttemptRepository;
import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
import io.paymentgateway.core.service.mapper.WebhookDeliveryAttemptMapper;
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
 * Integration tests for the {@link WebhookDeliveryAttemptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WebhookDeliveryAttemptResourceIT {

    private static final String DEFAULT_EVENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_TYPE = "BBBBBBBBBB";

    private static final WebhookDeliveryStatus DEFAULT_STATUS = WebhookDeliveryStatus.PENDING;
    private static final WebhookDeliveryStatus UPDATED_STATUS = WebhookDeliveryStatus.DELIVERED;

    private static final Integer DEFAULT_HTTP_STATUS_CODE = 1;
    private static final Integer UPDATED_HTTP_STATUS_CODE = 2;
    private static final Integer SMALLER_HTTP_STATUS_CODE = 1 - 1;

    private static final Integer DEFAULT_ATTEMPT_NUMBER = 1;
    private static final Integer UPDATED_ATTEMPT_NUMBER = 2;
    private static final Integer SMALLER_ATTEMPT_NUMBER = 1 - 1;

    private static final Instant DEFAULT_ATTEMPTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ATTEMPTED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/webhook-delivery-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository;

    @Autowired
    private WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWebhookDeliveryAttemptMockMvc;

    private WebhookDeliveryAttempt webhookDeliveryAttempt;

    private WebhookDeliveryAttempt insertedWebhookDeliveryAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookDeliveryAttempt createEntity(EntityManager em) {
        WebhookDeliveryAttempt webhookDeliveryAttempt = new WebhookDeliveryAttempt()
            .eventType(DEFAULT_EVENT_TYPE)
            .status(DEFAULT_STATUS)
            .httpStatusCode(DEFAULT_HTTP_STATUS_CODE)
            .attemptNumber(DEFAULT_ATTEMPT_NUMBER)
            .attemptedAt(DEFAULT_ATTEMPTED_AT);
        // Add required entity
        WebhookSubscription webhookSubscription;
        if (TestUtil.findAll(em, WebhookSubscription.class).isEmpty()) {
            webhookSubscription = WebhookSubscriptionResourceIT.createEntity(em);
            em.persist(webhookSubscription);
            em.flush();
        } else {
            webhookSubscription = TestUtil.findAll(em, WebhookSubscription.class).getFirst();
        }
        webhookDeliveryAttempt.setSubscription(webhookSubscription);
        return webhookDeliveryAttempt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookDeliveryAttempt createUpdatedEntity(EntityManager em) {
        WebhookDeliveryAttempt updatedWebhookDeliveryAttempt = new WebhookDeliveryAttempt()
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .httpStatusCode(UPDATED_HTTP_STATUS_CODE)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .attemptedAt(UPDATED_ATTEMPTED_AT);
        // Add required entity
        WebhookSubscription webhookSubscription;
        if (TestUtil.findAll(em, WebhookSubscription.class).isEmpty()) {
            webhookSubscription = WebhookSubscriptionResourceIT.createUpdatedEntity(em);
            em.persist(webhookSubscription);
            em.flush();
        } else {
            webhookSubscription = TestUtil.findAll(em, WebhookSubscription.class).getFirst();
        }
        updatedWebhookDeliveryAttempt.setSubscription(webhookSubscription);
        return updatedWebhookDeliveryAttempt;
    }

    @BeforeEach
    void initTest() {
        webhookDeliveryAttempt = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWebhookDeliveryAttempt != null) {
            webhookDeliveryAttemptRepository.delete(insertedWebhookDeliveryAttempt);
            insertedWebhookDeliveryAttempt = null;
        }
    }

    @Test
    @Transactional
    void createWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);
        var returnedWebhookDeliveryAttemptDTO = om.readValue(
            restWebhookDeliveryAttemptMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WebhookDeliveryAttemptDTO.class
        );

        // Validate the WebhookDeliveryAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWebhookDeliveryAttempt = webhookDeliveryAttemptMapper.toEntity(returnedWebhookDeliveryAttemptDTO);
        assertWebhookDeliveryAttemptUpdatableFieldsEquals(
            returnedWebhookDeliveryAttempt,
            getPersistedWebhookDeliveryAttempt(returnedWebhookDeliveryAttempt)
        );

        insertedWebhookDeliveryAttempt = returnedWebhookDeliveryAttempt;
    }

    @Test
    @Transactional
    void createWebhookDeliveryAttemptWithExistingId() throws Exception {
        // Create the WebhookDeliveryAttempt with an existing ID
        webhookDeliveryAttempt.setId(1L);
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWebhookDeliveryAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEventTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryAttempt.setEventType(null);

        // Create the WebhookDeliveryAttempt, which fails.
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        restWebhookDeliveryAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryAttempt.setStatus(null);

        // Create the WebhookDeliveryAttempt, which fails.
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        restWebhookDeliveryAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAttemptNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryAttempt.setAttemptNumber(null);

        // Create the WebhookDeliveryAttempt, which fails.
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        restWebhookDeliveryAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAttemptedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryAttempt.setAttemptedAt(null);

        // Create the WebhookDeliveryAttempt, which fails.
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        restWebhookDeliveryAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttempts() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookDeliveryAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].httpStatusCode").value(hasItem(DEFAULT_HTTP_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].attemptNumber").value(hasItem(DEFAULT_ATTEMPT_NUMBER)))
            .andExpect(jsonPath("$.[*].attemptedAt").value(hasItem(DEFAULT_ATTEMPTED_AT.toString())));
    }

    @Test
    @Transactional
    void getWebhookDeliveryAttempt() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get the webhookDeliveryAttempt
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, webhookDeliveryAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(webhookDeliveryAttempt.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.httpStatusCode").value(DEFAULT_HTTP_STATUS_CODE))
            .andExpect(jsonPath("$.attemptNumber").value(DEFAULT_ATTEMPT_NUMBER))
            .andExpect(jsonPath("$.attemptedAt").value(DEFAULT_ATTEMPTED_AT.toString()));
    }

    @Test
    @Transactional
    void getWebhookDeliveryAttemptsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        Long id = webhookDeliveryAttempt.getId();

        defaultWebhookDeliveryAttemptFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWebhookDeliveryAttemptFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWebhookDeliveryAttemptFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByEventTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where eventType equals to
        defaultWebhookDeliveryAttemptFiltering("eventType.equals=" + DEFAULT_EVENT_TYPE, "eventType.equals=" + UPDATED_EVENT_TYPE);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByEventTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where eventType in
        defaultWebhookDeliveryAttemptFiltering(
            "eventType.in=" + DEFAULT_EVENT_TYPE + "," + UPDATED_EVENT_TYPE,
            "eventType.in=" + UPDATED_EVENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByEventTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where eventType is not null
        defaultWebhookDeliveryAttemptFiltering("eventType.specified=true", "eventType.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByEventTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where eventType contains
        defaultWebhookDeliveryAttemptFiltering("eventType.contains=" + DEFAULT_EVENT_TYPE, "eventType.contains=" + UPDATED_EVENT_TYPE);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByEventTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where eventType does not contain
        defaultWebhookDeliveryAttemptFiltering(
            "eventType.doesNotContain=" + UPDATED_EVENT_TYPE,
            "eventType.doesNotContain=" + DEFAULT_EVENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where status equals to
        defaultWebhookDeliveryAttemptFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where status in
        defaultWebhookDeliveryAttemptFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where status is not null
        defaultWebhookDeliveryAttemptFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode equals to
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.equals=" + DEFAULT_HTTP_STATUS_CODE,
            "httpStatusCode.equals=" + UPDATED_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode in
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.in=" + DEFAULT_HTTP_STATUS_CODE + "," + UPDATED_HTTP_STATUS_CODE,
            "httpStatusCode.in=" + UPDATED_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode is not null
        defaultWebhookDeliveryAttemptFiltering("httpStatusCode.specified=true", "httpStatusCode.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode is greater than or equal to
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.greaterThanOrEqual=" + DEFAULT_HTTP_STATUS_CODE,
            "httpStatusCode.greaterThanOrEqual=" + UPDATED_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode is less than or equal to
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.lessThanOrEqual=" + DEFAULT_HTTP_STATUS_CODE,
            "httpStatusCode.lessThanOrEqual=" + SMALLER_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode is less than
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.lessThan=" + UPDATED_HTTP_STATUS_CODE,
            "httpStatusCode.lessThan=" + DEFAULT_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByHttpStatusCodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where httpStatusCode is greater than
        defaultWebhookDeliveryAttemptFiltering(
            "httpStatusCode.greaterThan=" + SMALLER_HTTP_STATUS_CODE,
            "httpStatusCode.greaterThan=" + DEFAULT_HTTP_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber equals to
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.equals=" + DEFAULT_ATTEMPT_NUMBER,
            "attemptNumber.equals=" + UPDATED_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber in
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.in=" + DEFAULT_ATTEMPT_NUMBER + "," + UPDATED_ATTEMPT_NUMBER,
            "attemptNumber.in=" + UPDATED_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber is not null
        defaultWebhookDeliveryAttemptFiltering("attemptNumber.specified=true", "attemptNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber is greater than or equal to
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.greaterThanOrEqual=" + DEFAULT_ATTEMPT_NUMBER,
            "attemptNumber.greaterThanOrEqual=" + UPDATED_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber is less than or equal to
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.lessThanOrEqual=" + DEFAULT_ATTEMPT_NUMBER,
            "attemptNumber.lessThanOrEqual=" + SMALLER_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber is less than
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.lessThan=" + UPDATED_ATTEMPT_NUMBER,
            "attemptNumber.lessThan=" + DEFAULT_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptNumber is greater than
        defaultWebhookDeliveryAttemptFiltering(
            "attemptNumber.greaterThan=" + SMALLER_ATTEMPT_NUMBER,
            "attemptNumber.greaterThan=" + DEFAULT_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptedAt equals to
        defaultWebhookDeliveryAttemptFiltering("attemptedAt.equals=" + DEFAULT_ATTEMPTED_AT, "attemptedAt.equals=" + UPDATED_ATTEMPTED_AT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptedAt in
        defaultWebhookDeliveryAttemptFiltering(
            "attemptedAt.in=" + DEFAULT_ATTEMPTED_AT + "," + UPDATED_ATTEMPTED_AT,
            "attemptedAt.in=" + UPDATED_ATTEMPTED_AT
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsByAttemptedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        // Get all the webhookDeliveryAttemptList where attemptedAt is not null
        defaultWebhookDeliveryAttemptFiltering("attemptedAt.specified=true", "attemptedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryAttemptsBySubscriptionIsEqualToSomething() throws Exception {
        WebhookSubscription subscription;
        if (TestUtil.findAll(em, WebhookSubscription.class).isEmpty()) {
            webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);
            subscription = WebhookSubscriptionResourceIT.createEntity(em);
        } else {
            subscription = TestUtil.findAll(em, WebhookSubscription.class).getFirst();
        }
        em.persist(subscription);
        em.flush();
        webhookDeliveryAttempt.setSubscription(subscription);
        webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);
        Long subscriptionId = subscription.getId();
        // Get all the webhookDeliveryAttemptList where subscription equals to subscriptionId
        defaultWebhookDeliveryAttemptShouldBeFound("subscriptionId.equals=" + subscriptionId);

        // Get all the webhookDeliveryAttemptList where subscription equals to (subscriptionId + 1)
        defaultWebhookDeliveryAttemptShouldNotBeFound("subscriptionId.equals=" + (subscriptionId + 1));
    }

    private void defaultWebhookDeliveryAttemptFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWebhookDeliveryAttemptShouldBeFound(shouldBeFound);
        defaultWebhookDeliveryAttemptShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWebhookDeliveryAttemptShouldBeFound(String filter) throws Exception {
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookDeliveryAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].httpStatusCode").value(hasItem(DEFAULT_HTTP_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].attemptNumber").value(hasItem(DEFAULT_ATTEMPT_NUMBER)))
            .andExpect(jsonPath("$.[*].attemptedAt").value(hasItem(DEFAULT_ATTEMPTED_AT.toString())));

        // Check, that the count call also returns 1
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWebhookDeliveryAttemptShouldNotBeFound(String filter) throws Exception {
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWebhookDeliveryAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWebhookDeliveryAttempt() throws Exception {
        // Get the webhookDeliveryAttempt
        restWebhookDeliveryAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWebhookDeliveryAttempt() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryAttempt
        WebhookDeliveryAttempt updatedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository
            .findById(webhookDeliveryAttempt.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedWebhookDeliveryAttempt are not directly saved in db
        em.detach(updatedWebhookDeliveryAttempt);
        updatedWebhookDeliveryAttempt
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .httpStatusCode(UPDATED_HTTP_STATUS_CODE)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .attemptedAt(UPDATED_ATTEMPTED_AT);
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(updatedWebhookDeliveryAttempt);

        restWebhookDeliveryAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookDeliveryAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWebhookDeliveryAttemptToMatchAllProperties(updatedWebhookDeliveryAttempt);
    }

    @Test
    @Transactional
    void putNonExistingWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookDeliveryAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWebhookDeliveryAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryAttempt using partial update
        WebhookDeliveryAttempt partialUpdatedWebhookDeliveryAttempt = new WebhookDeliveryAttempt();
        partialUpdatedWebhookDeliveryAttempt.setId(webhookDeliveryAttempt.getId());

        partialUpdatedWebhookDeliveryAttempt
            .status(UPDATED_STATUS)
            .httpStatusCode(UPDATED_HTTP_STATUS_CODE)
            .attemptedAt(UPDATED_ATTEMPTED_AT);

        restWebhookDeliveryAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookDeliveryAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookDeliveryAttempt))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookDeliveryAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWebhookDeliveryAttempt, webhookDeliveryAttempt),
            getPersistedWebhookDeliveryAttempt(webhookDeliveryAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateWebhookDeliveryAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryAttempt using partial update
        WebhookDeliveryAttempt partialUpdatedWebhookDeliveryAttempt = new WebhookDeliveryAttempt();
        partialUpdatedWebhookDeliveryAttempt.setId(webhookDeliveryAttempt.getId());

        partialUpdatedWebhookDeliveryAttempt
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .httpStatusCode(UPDATED_HTTP_STATUS_CODE)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .attemptedAt(UPDATED_ATTEMPTED_AT);

        restWebhookDeliveryAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookDeliveryAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookDeliveryAttempt))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookDeliveryAttemptUpdatableFieldsEquals(
            partialUpdatedWebhookDeliveryAttempt,
            getPersistedWebhookDeliveryAttempt(partialUpdatedWebhookDeliveryAttempt)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, webhookDeliveryAttemptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWebhookDeliveryAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryAttempt.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryAttempt
        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(webhookDeliveryAttemptDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookDeliveryAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWebhookDeliveryAttempt() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryAttempt = webhookDeliveryAttemptRepository.saveAndFlush(webhookDeliveryAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the webhookDeliveryAttempt
        restWebhookDeliveryAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, webhookDeliveryAttempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return webhookDeliveryAttemptRepository.count();
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

    protected WebhookDeliveryAttempt getPersistedWebhookDeliveryAttempt(WebhookDeliveryAttempt webhookDeliveryAttempt) {
        return webhookDeliveryAttemptRepository.findById(webhookDeliveryAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedWebhookDeliveryAttemptToMatchAllProperties(WebhookDeliveryAttempt expectedWebhookDeliveryAttempt) {
        assertWebhookDeliveryAttemptAllPropertiesEquals(
            expectedWebhookDeliveryAttempt,
            getPersistedWebhookDeliveryAttempt(expectedWebhookDeliveryAttempt)
        );
    }

    protected void assertPersistedWebhookDeliveryAttemptToMatchUpdatableProperties(WebhookDeliveryAttempt expectedWebhookDeliveryAttempt) {
        assertWebhookDeliveryAttemptAllUpdatablePropertiesEquals(
            expectedWebhookDeliveryAttempt,
            getPersistedWebhookDeliveryAttempt(expectedWebhookDeliveryAttempt)
        );
    }
}
