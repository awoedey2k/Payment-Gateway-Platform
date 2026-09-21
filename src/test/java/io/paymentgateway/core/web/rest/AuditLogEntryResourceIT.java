package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.AuditLogEntryAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.AuditLogEntry;
import io.paymentgateway.core.domain.enumeration.ActorType;
import io.paymentgateway.core.repository.AuditLogEntryRepository;
import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
import io.paymentgateway.core.service.mapper.AuditLogEntryMapper;
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
 * Integration tests for the {@link AuditLogEntryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuditLogEntryResourceIT {

    private static final ActorType DEFAULT_ACTOR_TYPE = ActorType.SYSTEM;
    private static final ActorType UPDATED_ACTOR_TYPE = ActorType.STAFF_USER;

    private static final String DEFAULT_ACTOR_ID = "AAAAAAAAAA";
    private static final String UPDATED_ACTOR_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_ID = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PREVIOUS_HASH = "AAAAAAAAAA";
    private static final String UPDATED_PREVIOUS_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_ENTRY_HASH = "AAAAAAAAAA";
    private static final String UPDATED_ENTRY_HASH = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECORDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECORDED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/audit-log-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditLogEntryRepository auditLogEntryRepository;

    @Autowired
    private AuditLogEntryMapper auditLogEntryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditLogEntryMockMvc;

    private AuditLogEntry auditLogEntry;

    private AuditLogEntry insertedAuditLogEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLogEntry createEntity() {
        return new AuditLogEntry()
            .actorType(DEFAULT_ACTOR_TYPE)
            .actorId(DEFAULT_ACTOR_ID)
            .action(DEFAULT_ACTION)
            .entityType(DEFAULT_ENTITY_TYPE)
            .entityId(DEFAULT_ENTITY_ID)
            .previousHash(DEFAULT_PREVIOUS_HASH)
            .entryHash(DEFAULT_ENTRY_HASH)
            .recordedAt(DEFAULT_RECORDED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLogEntry createUpdatedEntity() {
        return new AuditLogEntry()
            .actorType(UPDATED_ACTOR_TYPE)
            .actorId(UPDATED_ACTOR_ID)
            .action(UPDATED_ACTION)
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .previousHash(UPDATED_PREVIOUS_HASH)
            .entryHash(UPDATED_ENTRY_HASH)
            .recordedAt(UPDATED_RECORDED_AT);
    }

    @BeforeEach
    void initTest() {
        auditLogEntry = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditLogEntry != null) {
            auditLogEntryRepository.delete(insertedAuditLogEntry);
            insertedAuditLogEntry = null;
        }
    }

    @Test
    @Transactional
    void createAuditLogEntry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);
        var returnedAuditLogEntryDTO = om.readValue(
            restAuditLogEntryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuditLogEntryDTO.class
        );

        // Validate the AuditLogEntry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditLogEntry = auditLogEntryMapper.toEntity(returnedAuditLogEntryDTO);
        assertAuditLogEntryUpdatableFieldsEquals(returnedAuditLogEntry, getPersistedAuditLogEntry(returnedAuditLogEntry));

        insertedAuditLogEntry = returnedAuditLogEntry;
    }

    @Test
    @Transactional
    void createAuditLogEntryWithExistingId() throws Exception {
        // Create the AuditLogEntry with an existing ID
        auditLogEntry.setId(1L);
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActorTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setActorType(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setActorId(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setAction(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntityTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setEntityType(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntityIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setEntityId(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntryHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setEntryHash(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRecordedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLogEntry.setRecordedAt(null);

        // Create the AuditLogEntry, which fails.
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        restAuditLogEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAuditLogEntries() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditLogEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].actorType").value(hasItem(DEFAULT_ACTOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].actorId").value(hasItem(DEFAULT_ACTOR_ID)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID)))
            .andExpect(jsonPath("$.[*].previousHash").value(hasItem(DEFAULT_PREVIOUS_HASH)))
            .andExpect(jsonPath("$.[*].entryHash").value(hasItem(DEFAULT_ENTRY_HASH)))
            .andExpect(jsonPath("$.[*].recordedAt").value(hasItem(DEFAULT_RECORDED_AT.toString())));
    }

    @Test
    @Transactional
    void getAuditLogEntry() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get the auditLogEntry
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, auditLogEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditLogEntry.getId().intValue()))
            .andExpect(jsonPath("$.actorType").value(DEFAULT_ACTOR_TYPE.toString()))
            .andExpect(jsonPath("$.actorId").value(DEFAULT_ACTOR_ID))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.entityType").value(DEFAULT_ENTITY_TYPE))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID))
            .andExpect(jsonPath("$.previousHash").value(DEFAULT_PREVIOUS_HASH))
            .andExpect(jsonPath("$.entryHash").value(DEFAULT_ENTRY_HASH))
            .andExpect(jsonPath("$.recordedAt").value(DEFAULT_RECORDED_AT.toString()));
    }

    @Test
    @Transactional
    void getAuditLogEntriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        Long id = auditLogEntry.getId();

        defaultAuditLogEntryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuditLogEntryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuditLogEntryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorType equals to
        defaultAuditLogEntryFiltering("actorType.equals=" + DEFAULT_ACTOR_TYPE, "actorType.equals=" + UPDATED_ACTOR_TYPE);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorType in
        defaultAuditLogEntryFiltering(
            "actorType.in=" + DEFAULT_ACTOR_TYPE + "," + UPDATED_ACTOR_TYPE,
            "actorType.in=" + UPDATED_ACTOR_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorType is not null
        defaultAuditLogEntryFiltering("actorType.specified=true", "actorType.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorId equals to
        defaultAuditLogEntryFiltering("actorId.equals=" + DEFAULT_ACTOR_ID, "actorId.equals=" + UPDATED_ACTOR_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorId in
        defaultAuditLogEntryFiltering("actorId.in=" + DEFAULT_ACTOR_ID + "," + UPDATED_ACTOR_ID, "actorId.in=" + UPDATED_ACTOR_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorId is not null
        defaultAuditLogEntryFiltering("actorId.specified=true", "actorId.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorIdContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorId contains
        defaultAuditLogEntryFiltering("actorId.contains=" + DEFAULT_ACTOR_ID, "actorId.contains=" + UPDATED_ACTOR_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActorIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where actorId does not contain
        defaultAuditLogEntryFiltering("actorId.doesNotContain=" + UPDATED_ACTOR_ID, "actorId.doesNotContain=" + DEFAULT_ACTOR_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where action equals to
        defaultAuditLogEntryFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where action in
        defaultAuditLogEntryFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where action is not null
        defaultAuditLogEntryFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActionContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where action contains
        defaultAuditLogEntryFiltering("action.contains=" + DEFAULT_ACTION, "action.contains=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByActionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where action does not contain
        defaultAuditLogEntryFiltering("action.doesNotContain=" + UPDATED_ACTION, "action.doesNotContain=" + DEFAULT_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityType equals to
        defaultAuditLogEntryFiltering("entityType.equals=" + DEFAULT_ENTITY_TYPE, "entityType.equals=" + UPDATED_ENTITY_TYPE);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityType in
        defaultAuditLogEntryFiltering(
            "entityType.in=" + DEFAULT_ENTITY_TYPE + "," + UPDATED_ENTITY_TYPE,
            "entityType.in=" + UPDATED_ENTITY_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityType is not null
        defaultAuditLogEntryFiltering("entityType.specified=true", "entityType.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityType contains
        defaultAuditLogEntryFiltering("entityType.contains=" + DEFAULT_ENTITY_TYPE, "entityType.contains=" + UPDATED_ENTITY_TYPE);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityType does not contain
        defaultAuditLogEntryFiltering(
            "entityType.doesNotContain=" + UPDATED_ENTITY_TYPE,
            "entityType.doesNotContain=" + DEFAULT_ENTITY_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityId equals to
        defaultAuditLogEntryFiltering("entityId.equals=" + DEFAULT_ENTITY_ID, "entityId.equals=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityId in
        defaultAuditLogEntryFiltering("entityId.in=" + DEFAULT_ENTITY_ID + "," + UPDATED_ENTITY_ID, "entityId.in=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityId is not null
        defaultAuditLogEntryFiltering("entityId.specified=true", "entityId.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityIdContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityId contains
        defaultAuditLogEntryFiltering("entityId.contains=" + DEFAULT_ENTITY_ID, "entityId.contains=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntityIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entityId does not contain
        defaultAuditLogEntryFiltering("entityId.doesNotContain=" + UPDATED_ENTITY_ID, "entityId.doesNotContain=" + DEFAULT_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByPreviousHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where previousHash equals to
        defaultAuditLogEntryFiltering("previousHash.equals=" + DEFAULT_PREVIOUS_HASH, "previousHash.equals=" + UPDATED_PREVIOUS_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByPreviousHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where previousHash in
        defaultAuditLogEntryFiltering(
            "previousHash.in=" + DEFAULT_PREVIOUS_HASH + "," + UPDATED_PREVIOUS_HASH,
            "previousHash.in=" + UPDATED_PREVIOUS_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByPreviousHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where previousHash is not null
        defaultAuditLogEntryFiltering("previousHash.specified=true", "previousHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByPreviousHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where previousHash contains
        defaultAuditLogEntryFiltering("previousHash.contains=" + DEFAULT_PREVIOUS_HASH, "previousHash.contains=" + UPDATED_PREVIOUS_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByPreviousHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where previousHash does not contain
        defaultAuditLogEntryFiltering(
            "previousHash.doesNotContain=" + UPDATED_PREVIOUS_HASH,
            "previousHash.doesNotContain=" + DEFAULT_PREVIOUS_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntryHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entryHash equals to
        defaultAuditLogEntryFiltering("entryHash.equals=" + DEFAULT_ENTRY_HASH, "entryHash.equals=" + UPDATED_ENTRY_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntryHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entryHash in
        defaultAuditLogEntryFiltering(
            "entryHash.in=" + DEFAULT_ENTRY_HASH + "," + UPDATED_ENTRY_HASH,
            "entryHash.in=" + UPDATED_ENTRY_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntryHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entryHash is not null
        defaultAuditLogEntryFiltering("entryHash.specified=true", "entryHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntryHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entryHash contains
        defaultAuditLogEntryFiltering("entryHash.contains=" + DEFAULT_ENTRY_HASH, "entryHash.contains=" + UPDATED_ENTRY_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByEntryHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where entryHash does not contain
        defaultAuditLogEntryFiltering("entryHash.doesNotContain=" + UPDATED_ENTRY_HASH, "entryHash.doesNotContain=" + DEFAULT_ENTRY_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByRecordedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where recordedAt equals to
        defaultAuditLogEntryFiltering("recordedAt.equals=" + DEFAULT_RECORDED_AT, "recordedAt.equals=" + UPDATED_RECORDED_AT);
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByRecordedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where recordedAt in
        defaultAuditLogEntryFiltering(
            "recordedAt.in=" + DEFAULT_RECORDED_AT + "," + UPDATED_RECORDED_AT,
            "recordedAt.in=" + UPDATED_RECORDED_AT
        );
    }

    @Test
    @Transactional
    void getAllAuditLogEntriesByRecordedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        // Get all the auditLogEntryList where recordedAt is not null
        defaultAuditLogEntryFiltering("recordedAt.specified=true", "recordedAt.specified=false");
    }

    private void defaultAuditLogEntryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuditLogEntryShouldBeFound(shouldBeFound);
        defaultAuditLogEntryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuditLogEntryShouldBeFound(String filter) throws Exception {
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditLogEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].actorType").value(hasItem(DEFAULT_ACTOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].actorId").value(hasItem(DEFAULT_ACTOR_ID)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID)))
            .andExpect(jsonPath("$.[*].previousHash").value(hasItem(DEFAULT_PREVIOUS_HASH)))
            .andExpect(jsonPath("$.[*].entryHash").value(hasItem(DEFAULT_ENTRY_HASH)))
            .andExpect(jsonPath("$.[*].recordedAt").value(hasItem(DEFAULT_RECORDED_AT.toString())));

        // Check, that the count call also returns 1
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuditLogEntryShouldNotBeFound(String filter) throws Exception {
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuditLogEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuditLogEntry() throws Exception {
        // Get the auditLogEntry
        restAuditLogEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuditLogEntry() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLogEntry
        AuditLogEntry updatedAuditLogEntry = auditLogEntryRepository.findById(auditLogEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuditLogEntry are not directly saved in db
        em.detach(updatedAuditLogEntry);
        updatedAuditLogEntry
            .actorType(UPDATED_ACTOR_TYPE)
            .actorId(UPDATED_ACTOR_ID)
            .action(UPDATED_ACTION)
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .previousHash(UPDATED_PREVIOUS_HASH)
            .entryHash(UPDATED_ENTRY_HASH)
            .recordedAt(UPDATED_RECORDED_AT);
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(updatedAuditLogEntry);

        restAuditLogEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditLogEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditLogEntryToMatchAllProperties(updatedAuditLogEntry);
    }

    @Test
    @Transactional
    void putNonExistingAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditLogEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuditLogEntryWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLogEntry using partial update
        AuditLogEntry partialUpdatedAuditLogEntry = new AuditLogEntry();
        partialUpdatedAuditLogEntry.setId(auditLogEntry.getId());

        partialUpdatedAuditLogEntry.action(UPDATED_ACTION).recordedAt(UPDATED_RECORDED_AT);

        restAuditLogEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditLogEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditLogEntry))
            )
            .andExpect(status().isOk());

        // Validate the AuditLogEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogEntryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditLogEntry, auditLogEntry),
            getPersistedAuditLogEntry(auditLogEntry)
        );
    }

    @Test
    @Transactional
    void fullUpdateAuditLogEntryWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLogEntry using partial update
        AuditLogEntry partialUpdatedAuditLogEntry = new AuditLogEntry();
        partialUpdatedAuditLogEntry.setId(auditLogEntry.getId());

        partialUpdatedAuditLogEntry
            .actorType(UPDATED_ACTOR_TYPE)
            .actorId(UPDATED_ACTOR_ID)
            .action(UPDATED_ACTION)
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .previousHash(UPDATED_PREVIOUS_HASH)
            .entryHash(UPDATED_ENTRY_HASH)
            .recordedAt(UPDATED_RECORDED_AT);

        restAuditLogEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditLogEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditLogEntry))
            )
            .andExpect(status().isOk());

        // Validate the AuditLogEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogEntryUpdatableFieldsEquals(partialUpdatedAuditLogEntry, getPersistedAuditLogEntry(partialUpdatedAuditLogEntry));
    }

    @Test
    @Transactional
    void patchNonExistingAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditLogEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditLogEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditLogEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditLogEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLogEntry.setId(longCount.incrementAndGet());

        // Create the AuditLogEntry
        AuditLogEntryDTO auditLogEntryDTO = auditLogEntryMapper.toDto(auditLogEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogEntryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auditLogEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditLogEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuditLogEntry() throws Exception {
        // Initialize the database
        insertedAuditLogEntry = auditLogEntryRepository.saveAndFlush(auditLogEntry);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditLogEntry
        restAuditLogEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditLogEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditLogEntryRepository.count();
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

    protected AuditLogEntry getPersistedAuditLogEntry(AuditLogEntry auditLogEntry) {
        return auditLogEntryRepository.findById(auditLogEntry.getId()).orElseThrow();
    }

    protected void assertPersistedAuditLogEntryToMatchAllProperties(AuditLogEntry expectedAuditLogEntry) {
        assertAuditLogEntryAllPropertiesEquals(expectedAuditLogEntry, getPersistedAuditLogEntry(expectedAuditLogEntry));
    }

    protected void assertPersistedAuditLogEntryToMatchUpdatableProperties(AuditLogEntry expectedAuditLogEntry) {
        assertAuditLogEntryAllUpdatablePropertiesEquals(expectedAuditLogEntry, getPersistedAuditLogEntry(expectedAuditLogEntry));
    }
}
