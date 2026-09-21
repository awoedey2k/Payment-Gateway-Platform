package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.AmlCheckAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.AmlCheck;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.domain.enumeration.AmlDecision;
import io.paymentgateway.core.repository.AmlCheckRepository;
import io.paymentgateway.core.service.dto.AmlCheckDTO;
import io.paymentgateway.core.service.mapper.AmlCheckMapper;
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
 * Integration tests for the {@link AmlCheckResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AmlCheckResourceIT {

    private static final Integer DEFAULT_RISK_SCORE = 1;
    private static final Integer UPDATED_RISK_SCORE = 2;
    private static final Integer SMALLER_RISK_SCORE = 1 - 1;

    private static final AmlDecision DEFAULT_DECISION = AmlDecision.ALLOW;
    private static final AmlDecision UPDATED_DECISION = AmlDecision.CHALLENGE;

    private static final String DEFAULT_RULE_TRIGGERED = "AAAAAAAAAA";
    private static final String UPDATED_RULE_TRIGGERED = "BBBBBBBBBB";

    private static final Instant DEFAULT_CHECKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECKED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/aml-checks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AmlCheckRepository amlCheckRepository;

    @Autowired
    private AmlCheckMapper amlCheckMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAmlCheckMockMvc;

    private AmlCheck amlCheck;

    private AmlCheck insertedAmlCheck;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AmlCheck createEntity() {
        return new AmlCheck()
            .riskScore(DEFAULT_RISK_SCORE)
            .decision(DEFAULT_DECISION)
            .ruleTriggered(DEFAULT_RULE_TRIGGERED)
            .checkedAt(DEFAULT_CHECKED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AmlCheck createUpdatedEntity() {
        return new AmlCheck()
            .riskScore(UPDATED_RISK_SCORE)
            .decision(UPDATED_DECISION)
            .ruleTriggered(UPDATED_RULE_TRIGGERED)
            .checkedAt(UPDATED_CHECKED_AT);
    }

    @BeforeEach
    void initTest() {
        amlCheck = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAmlCheck != null) {
            amlCheckRepository.delete(insertedAmlCheck);
            insertedAmlCheck = null;
        }
    }

    @Test
    @Transactional
    void createAmlCheck() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);
        var returnedAmlCheckDTO = om.readValue(
            restAmlCheckMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AmlCheckDTO.class
        );

        // Validate the AmlCheck in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAmlCheck = amlCheckMapper.toEntity(returnedAmlCheckDTO);
        assertAmlCheckUpdatableFieldsEquals(returnedAmlCheck, getPersistedAmlCheck(returnedAmlCheck));

        insertedAmlCheck = returnedAmlCheck;
    }

    @Test
    @Transactional
    void createAmlCheckWithExistingId() throws Exception {
        // Create the AmlCheck with an existing ID
        amlCheck.setId(1L);
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAmlCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRiskScoreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amlCheck.setRiskScore(null);

        // Create the AmlCheck, which fails.
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        restAmlCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDecisionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amlCheck.setDecision(null);

        // Create the AmlCheck, which fails.
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        restAmlCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCheckedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amlCheck.setCheckedAt(null);

        // Create the AmlCheck, which fails.
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        restAmlCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAmlChecks() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amlCheck.getId().intValue())))
            .andExpect(jsonPath("$.[*].riskScore").value(hasItem(DEFAULT_RISK_SCORE)))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION.toString())))
            .andExpect(jsonPath("$.[*].ruleTriggered").value(hasItem(DEFAULT_RULE_TRIGGERED)))
            .andExpect(jsonPath("$.[*].checkedAt").value(hasItem(DEFAULT_CHECKED_AT.toString())));
    }

    @Test
    @Transactional
    void getAmlCheck() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get the amlCheck
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL_ID, amlCheck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(amlCheck.getId().intValue()))
            .andExpect(jsonPath("$.riskScore").value(DEFAULT_RISK_SCORE))
            .andExpect(jsonPath("$.decision").value(DEFAULT_DECISION.toString()))
            .andExpect(jsonPath("$.ruleTriggered").value(DEFAULT_RULE_TRIGGERED))
            .andExpect(jsonPath("$.checkedAt").value(DEFAULT_CHECKED_AT.toString()));
    }

    @Test
    @Transactional
    void getAmlChecksByIdFiltering() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        Long id = amlCheck.getId();

        defaultAmlCheckFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAmlCheckFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAmlCheckFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore equals to
        defaultAmlCheckFiltering("riskScore.equals=" + DEFAULT_RISK_SCORE, "riskScore.equals=" + UPDATED_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore in
        defaultAmlCheckFiltering("riskScore.in=" + DEFAULT_RISK_SCORE + "," + UPDATED_RISK_SCORE, "riskScore.in=" + UPDATED_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore is not null
        defaultAmlCheckFiltering("riskScore.specified=true", "riskScore.specified=false");
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore is greater than or equal to
        defaultAmlCheckFiltering(
            "riskScore.greaterThanOrEqual=" + DEFAULT_RISK_SCORE,
            "riskScore.greaterThanOrEqual=" + UPDATED_RISK_SCORE
        );
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore is less than or equal to
        defaultAmlCheckFiltering("riskScore.lessThanOrEqual=" + DEFAULT_RISK_SCORE, "riskScore.lessThanOrEqual=" + SMALLER_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore is less than
        defaultAmlCheckFiltering("riskScore.lessThan=" + UPDATED_RISK_SCORE, "riskScore.lessThan=" + DEFAULT_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRiskScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where riskScore is greater than
        defaultAmlCheckFiltering("riskScore.greaterThan=" + SMALLER_RISK_SCORE, "riskScore.greaterThan=" + DEFAULT_RISK_SCORE);
    }

    @Test
    @Transactional
    void getAllAmlChecksByDecisionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where decision equals to
        defaultAmlCheckFiltering("decision.equals=" + DEFAULT_DECISION, "decision.equals=" + UPDATED_DECISION);
    }

    @Test
    @Transactional
    void getAllAmlChecksByDecisionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where decision in
        defaultAmlCheckFiltering("decision.in=" + DEFAULT_DECISION + "," + UPDATED_DECISION, "decision.in=" + UPDATED_DECISION);
    }

    @Test
    @Transactional
    void getAllAmlChecksByDecisionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where decision is not null
        defaultAmlCheckFiltering("decision.specified=true", "decision.specified=false");
    }

    @Test
    @Transactional
    void getAllAmlChecksByRuleTriggeredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where ruleTriggered equals to
        defaultAmlCheckFiltering("ruleTriggered.equals=" + DEFAULT_RULE_TRIGGERED, "ruleTriggered.equals=" + UPDATED_RULE_TRIGGERED);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRuleTriggeredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where ruleTriggered in
        defaultAmlCheckFiltering(
            "ruleTriggered.in=" + DEFAULT_RULE_TRIGGERED + "," + UPDATED_RULE_TRIGGERED,
            "ruleTriggered.in=" + UPDATED_RULE_TRIGGERED
        );
    }

    @Test
    @Transactional
    void getAllAmlChecksByRuleTriggeredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where ruleTriggered is not null
        defaultAmlCheckFiltering("ruleTriggered.specified=true", "ruleTriggered.specified=false");
    }

    @Test
    @Transactional
    void getAllAmlChecksByRuleTriggeredContainsSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where ruleTriggered contains
        defaultAmlCheckFiltering("ruleTriggered.contains=" + DEFAULT_RULE_TRIGGERED, "ruleTriggered.contains=" + UPDATED_RULE_TRIGGERED);
    }

    @Test
    @Transactional
    void getAllAmlChecksByRuleTriggeredNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where ruleTriggered does not contain
        defaultAmlCheckFiltering(
            "ruleTriggered.doesNotContain=" + UPDATED_RULE_TRIGGERED,
            "ruleTriggered.doesNotContain=" + DEFAULT_RULE_TRIGGERED
        );
    }

    @Test
    @Transactional
    void getAllAmlChecksByCheckedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where checkedAt equals to
        defaultAmlCheckFiltering("checkedAt.equals=" + DEFAULT_CHECKED_AT, "checkedAt.equals=" + UPDATED_CHECKED_AT);
    }

    @Test
    @Transactional
    void getAllAmlChecksByCheckedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where checkedAt in
        defaultAmlCheckFiltering("checkedAt.in=" + DEFAULT_CHECKED_AT + "," + UPDATED_CHECKED_AT, "checkedAt.in=" + UPDATED_CHECKED_AT);
    }

    @Test
    @Transactional
    void getAllAmlChecksByCheckedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        // Get all the amlCheckList where checkedAt is not null
        defaultAmlCheckFiltering("checkedAt.specified=true", "checkedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAmlChecksByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            amlCheckRepository.saveAndFlush(amlCheck);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        amlCheck.setTenant(tenant);
        amlCheckRepository.saveAndFlush(amlCheck);
        Long tenantId = tenant.getId();
        // Get all the amlCheckList where tenant equals to tenantId
        defaultAmlCheckShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the amlCheckList where tenant equals to (tenantId + 1)
        defaultAmlCheckShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    @Test
    @Transactional
    void getAllAmlChecksByTransactionIsEqualToSomething() throws Exception {
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            amlCheckRepository.saveAndFlush(amlCheck);
            transaction = TransactionResourceIT.createEntity(em);
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        em.persist(transaction);
        em.flush();
        amlCheck.setTransaction(transaction);
        amlCheckRepository.saveAndFlush(amlCheck);
        Long transactionId = transaction.getId();
        // Get all the amlCheckList where transaction equals to transactionId
        defaultAmlCheckShouldBeFound("transactionId.equals=" + transactionId);

        // Get all the amlCheckList where transaction equals to (transactionId + 1)
        defaultAmlCheckShouldNotBeFound("transactionId.equals=" + (transactionId + 1));
    }

    private void defaultAmlCheckFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAmlCheckShouldBeFound(shouldBeFound);
        defaultAmlCheckShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAmlCheckShouldBeFound(String filter) throws Exception {
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amlCheck.getId().intValue())))
            .andExpect(jsonPath("$.[*].riskScore").value(hasItem(DEFAULT_RISK_SCORE)))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION.toString())))
            .andExpect(jsonPath("$.[*].ruleTriggered").value(hasItem(DEFAULT_RULE_TRIGGERED)))
            .andExpect(jsonPath("$.[*].checkedAt").value(hasItem(DEFAULT_CHECKED_AT.toString())));

        // Check, that the count call also returns 1
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAmlCheckShouldNotBeFound(String filter) throws Exception {
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAmlCheckMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAmlCheck() throws Exception {
        // Get the amlCheck
        restAmlCheckMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAmlCheck() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amlCheck
        AmlCheck updatedAmlCheck = amlCheckRepository.findById(amlCheck.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAmlCheck are not directly saved in db
        em.detach(updatedAmlCheck);
        updatedAmlCheck
            .riskScore(UPDATED_RISK_SCORE)
            .decision(UPDATED_DECISION)
            .ruleTriggered(UPDATED_RULE_TRIGGERED)
            .checkedAt(UPDATED_CHECKED_AT);
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(updatedAmlCheck);

        restAmlCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amlCheckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amlCheckDTO))
            )
            .andExpect(status().isOk());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAmlCheckToMatchAllProperties(updatedAmlCheck);
    }

    @Test
    @Transactional
    void putNonExistingAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amlCheckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amlCheckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amlCheckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAmlCheckWithPatch() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amlCheck using partial update
        AmlCheck partialUpdatedAmlCheck = new AmlCheck();
        partialUpdatedAmlCheck.setId(amlCheck.getId());

        partialUpdatedAmlCheck.riskScore(UPDATED_RISK_SCORE).decision(UPDATED_DECISION).checkedAt(UPDATED_CHECKED_AT);

        restAmlCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmlCheck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmlCheck))
            )
            .andExpect(status().isOk());

        // Validate the AmlCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmlCheckUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAmlCheck, amlCheck), getPersistedAmlCheck(amlCheck));
    }

    @Test
    @Transactional
    void fullUpdateAmlCheckWithPatch() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amlCheck using partial update
        AmlCheck partialUpdatedAmlCheck = new AmlCheck();
        partialUpdatedAmlCheck.setId(amlCheck.getId());

        partialUpdatedAmlCheck
            .riskScore(UPDATED_RISK_SCORE)
            .decision(UPDATED_DECISION)
            .ruleTriggered(UPDATED_RULE_TRIGGERED)
            .checkedAt(UPDATED_CHECKED_AT);

        restAmlCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmlCheck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmlCheck))
            )
            .andExpect(status().isOk());

        // Validate the AmlCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmlCheckUpdatableFieldsEquals(partialUpdatedAmlCheck, getPersistedAmlCheck(partialUpdatedAmlCheck));
    }

    @Test
    @Transactional
    void patchNonExistingAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, amlCheckDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amlCheckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amlCheckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAmlCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amlCheck.setId(longCount.incrementAndGet());

        // Create the AmlCheck
        AmlCheckDTO amlCheckDTO = amlCheckMapper.toDto(amlCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmlCheckMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(amlCheckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AmlCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAmlCheck() throws Exception {
        // Initialize the database
        insertedAmlCheck = amlCheckRepository.saveAndFlush(amlCheck);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the amlCheck
        restAmlCheckMockMvc
            .perform(delete(ENTITY_API_URL_ID, amlCheck.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return amlCheckRepository.count();
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

    protected AmlCheck getPersistedAmlCheck(AmlCheck amlCheck) {
        return amlCheckRepository.findById(amlCheck.getId()).orElseThrow();
    }

    protected void assertPersistedAmlCheckToMatchAllProperties(AmlCheck expectedAmlCheck) {
        assertAmlCheckAllPropertiesEquals(expectedAmlCheck, getPersistedAmlCheck(expectedAmlCheck));
    }

    protected void assertPersistedAmlCheckToMatchUpdatableProperties(AmlCheck expectedAmlCheck) {
        assertAmlCheckAllUpdatablePropertiesEquals(expectedAmlCheck, getPersistedAmlCheck(expectedAmlCheck));
    }
}
