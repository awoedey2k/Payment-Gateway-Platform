package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.DisputeAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.domain.enumeration.DisputeStatus;
import io.paymentgateway.core.repository.DisputeRepository;
import io.paymentgateway.core.service.dto.DisputeDTO;
import io.paymentgateway.core.service.mapper.DisputeMapper;
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
 * Integration tests for the {@link DisputeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisputeResourceIT {

    private static final String DEFAULT_CASE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_CASE_REFERENCE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_REASON_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REASON_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_REASON_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_REASON_DESCRIPTION = "BBBBBBBBBB";

    private static final DisputeStatus DEFAULT_STATUS = DisputeStatus.OPEN;
    private static final DisputeStatus UPDATED_STATUS = DisputeStatus.EVIDENCE_SUBMITTED;

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_EVIDENCE_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVIDENCE_SUBMITTED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_RESOLVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/disputes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisputeRepository disputeRepository;

    @Autowired
    private DisputeMapper disputeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisputeMockMvc;

    private Dispute dispute;

    private Dispute insertedDispute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispute createEntity(EntityManager em) {
        Dispute dispute = new Dispute()
            .caseReference(DEFAULT_CASE_REFERENCE)
            .amount(DEFAULT_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .reasonCode(DEFAULT_REASON_CODE)
            .reasonDescription(DEFAULT_REASON_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .dueDate(DEFAULT_DUE_DATE)
            .evidenceSubmittedAt(DEFAULT_EVIDENCE_SUBMITTED_AT)
            .resolvedAt(DEFAULT_RESOLVED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        dispute.setTenant(corporateTenant);
        // Add required entity
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            transaction = TransactionResourceIT.createEntity(em);
            em.persist(transaction);
            em.flush();
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        dispute.setTransaction(transaction);
        return dispute;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispute createUpdatedEntity(EntityManager em) {
        Dispute updatedDispute = new Dispute()
            .caseReference(UPDATED_CASE_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .reasonCode(UPDATED_REASON_CODE)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .evidenceSubmittedAt(UPDATED_EVIDENCE_SUBMITTED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedDispute.setTenant(corporateTenant);
        // Add required entity
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            transaction = TransactionResourceIT.createUpdatedEntity(em);
            em.persist(transaction);
            em.flush();
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        updatedDispute.setTransaction(transaction);
        return updatedDispute;
    }

    @BeforeEach
    void initTest() {
        dispute = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDispute != null) {
            disputeRepository.delete(insertedDispute);
            insertedDispute = null;
        }
    }

    @Test
    @Transactional
    void createDispute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);
        var returnedDisputeDTO = om.readValue(
            restDisputeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DisputeDTO.class
        );

        // Validate the Dispute in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDispute = disputeMapper.toEntity(returnedDisputeDTO);
        assertDisputeUpdatableFieldsEquals(returnedDispute, getPersistedDispute(returnedDispute));

        insertedDispute = returnedDispute;
    }

    @Test
    @Transactional
    void createDisputeWithExistingId() throws Exception {
        // Create the Dispute with an existing ID
        dispute.setId(1L);
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCaseReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setCaseReference(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setAmount(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setCurrencyCode(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setReasonCode(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setStatus(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setDueDate(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        restDisputeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDisputes() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dispute.getId().intValue())))
            .andExpect(jsonPath("$.[*].caseReference").value(hasItem(DEFAULT_CASE_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].reasonCode").value(hasItem(DEFAULT_REASON_CODE)))
            .andExpect(jsonPath("$.[*].reasonDescription").value(hasItem(DEFAULT_REASON_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].evidenceSubmittedAt").value(hasItem(DEFAULT_EVIDENCE_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())));
    }

    @Test
    @Transactional
    void getDispute() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get the dispute
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL_ID, dispute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dispute.getId().intValue()))
            .andExpect(jsonPath("$.caseReference").value(DEFAULT_CASE_REFERENCE))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.reasonCode").value(DEFAULT_REASON_CODE))
            .andExpect(jsonPath("$.reasonDescription").value(DEFAULT_REASON_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.evidenceSubmittedAt").value(DEFAULT_EVIDENCE_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.resolvedAt").value(DEFAULT_RESOLVED_AT.toString()));
    }

    @Test
    @Transactional
    void getDisputesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        Long id = dispute.getId();

        defaultDisputeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDisputeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDisputeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDisputesByCaseReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where caseReference equals to
        defaultDisputeFiltering("caseReference.equals=" + DEFAULT_CASE_REFERENCE, "caseReference.equals=" + UPDATED_CASE_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDisputesByCaseReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where caseReference in
        defaultDisputeFiltering(
            "caseReference.in=" + DEFAULT_CASE_REFERENCE + "," + UPDATED_CASE_REFERENCE,
            "caseReference.in=" + UPDATED_CASE_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllDisputesByCaseReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where caseReference is not null
        defaultDisputeFiltering("caseReference.specified=true", "caseReference.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByCaseReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where caseReference contains
        defaultDisputeFiltering("caseReference.contains=" + DEFAULT_CASE_REFERENCE, "caseReference.contains=" + UPDATED_CASE_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDisputesByCaseReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where caseReference does not contain
        defaultDisputeFiltering(
            "caseReference.doesNotContain=" + UPDATED_CASE_REFERENCE,
            "caseReference.doesNotContain=" + DEFAULT_CASE_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount equals to
        defaultDisputeFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount in
        defaultDisputeFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount is not null
        defaultDisputeFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount is greater than or equal to
        defaultDisputeFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount is less than or equal to
        defaultDisputeFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount is less than
        defaultDisputeFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where amount is greater than
        defaultDisputeFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDisputesByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where currencyCode equals to
        defaultDisputeFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where currencyCode in
        defaultDisputeFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllDisputesByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where currencyCode is not null
        defaultDisputeFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where currencyCode contains
        defaultDisputeFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where currencyCode does not contain
        defaultDisputeFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllDisputesByReasonCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonCode equals to
        defaultDisputeFiltering("reasonCode.equals=" + DEFAULT_REASON_CODE, "reasonCode.equals=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByReasonCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonCode in
        defaultDisputeFiltering("reasonCode.in=" + DEFAULT_REASON_CODE + "," + UPDATED_REASON_CODE, "reasonCode.in=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByReasonCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonCode is not null
        defaultDisputeFiltering("reasonCode.specified=true", "reasonCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByReasonCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonCode contains
        defaultDisputeFiltering("reasonCode.contains=" + DEFAULT_REASON_CODE, "reasonCode.contains=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByReasonCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonCode does not contain
        defaultDisputeFiltering("reasonCode.doesNotContain=" + UPDATED_REASON_CODE, "reasonCode.doesNotContain=" + DEFAULT_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllDisputesByReasonDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonDescription equals to
        defaultDisputeFiltering(
            "reasonDescription.equals=" + DEFAULT_REASON_DESCRIPTION,
            "reasonDescription.equals=" + UPDATED_REASON_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDisputesByReasonDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonDescription in
        defaultDisputeFiltering(
            "reasonDescription.in=" + DEFAULT_REASON_DESCRIPTION + "," + UPDATED_REASON_DESCRIPTION,
            "reasonDescription.in=" + UPDATED_REASON_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDisputesByReasonDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonDescription is not null
        defaultDisputeFiltering("reasonDescription.specified=true", "reasonDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByReasonDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonDescription contains
        defaultDisputeFiltering(
            "reasonDescription.contains=" + DEFAULT_REASON_DESCRIPTION,
            "reasonDescription.contains=" + UPDATED_REASON_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDisputesByReasonDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where reasonDescription does not contain
        defaultDisputeFiltering(
            "reasonDescription.doesNotContain=" + UPDATED_REASON_DESCRIPTION,
            "reasonDescription.doesNotContain=" + DEFAULT_REASON_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDisputesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where status equals to
        defaultDisputeFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDisputesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where status in
        defaultDisputeFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDisputesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where status is not null
        defaultDisputeFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where dueDate equals to
        defaultDisputeFiltering("dueDate.equals=" + DEFAULT_DUE_DATE, "dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllDisputesByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where dueDate in
        defaultDisputeFiltering("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE, "dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllDisputesByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where dueDate is not null
        defaultDisputeFiltering("dueDate.specified=true", "dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByEvidenceSubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where evidenceSubmittedAt equals to
        defaultDisputeFiltering(
            "evidenceSubmittedAt.equals=" + DEFAULT_EVIDENCE_SUBMITTED_AT,
            "evidenceSubmittedAt.equals=" + UPDATED_EVIDENCE_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllDisputesByEvidenceSubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where evidenceSubmittedAt in
        defaultDisputeFiltering(
            "evidenceSubmittedAt.in=" + DEFAULT_EVIDENCE_SUBMITTED_AT + "," + UPDATED_EVIDENCE_SUBMITTED_AT,
            "evidenceSubmittedAt.in=" + UPDATED_EVIDENCE_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllDisputesByEvidenceSubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where evidenceSubmittedAt is not null
        defaultDisputeFiltering("evidenceSubmittedAt.specified=true", "evidenceSubmittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByResolvedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where resolvedAt equals to
        defaultDisputeFiltering("resolvedAt.equals=" + DEFAULT_RESOLVED_AT, "resolvedAt.equals=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllDisputesByResolvedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where resolvedAt in
        defaultDisputeFiltering("resolvedAt.in=" + DEFAULT_RESOLVED_AT + "," + UPDATED_RESOLVED_AT, "resolvedAt.in=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllDisputesByResolvedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        // Get all the disputeList where resolvedAt is not null
        defaultDisputeFiltering("resolvedAt.specified=true", "resolvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputesByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            disputeRepository.saveAndFlush(dispute);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        dispute.setTenant(tenant);
        disputeRepository.saveAndFlush(dispute);
        Long tenantId = tenant.getId();
        // Get all the disputeList where tenant equals to tenantId
        defaultDisputeShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the disputeList where tenant equals to (tenantId + 1)
        defaultDisputeShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    @Test
    @Transactional
    void getAllDisputesByTransactionIsEqualToSomething() throws Exception {
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            disputeRepository.saveAndFlush(dispute);
            transaction = TransactionResourceIT.createEntity(em);
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        em.persist(transaction);
        em.flush();
        dispute.setTransaction(transaction);
        disputeRepository.saveAndFlush(dispute);
        Long transactionId = transaction.getId();
        // Get all the disputeList where transaction equals to transactionId
        defaultDisputeShouldBeFound("transactionId.equals=" + transactionId);

        // Get all the disputeList where transaction equals to (transactionId + 1)
        defaultDisputeShouldNotBeFound("transactionId.equals=" + (transactionId + 1));
    }

    private void defaultDisputeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDisputeShouldBeFound(shouldBeFound);
        defaultDisputeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDisputeShouldBeFound(String filter) throws Exception {
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dispute.getId().intValue())))
            .andExpect(jsonPath("$.[*].caseReference").value(hasItem(DEFAULT_CASE_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].reasonCode").value(hasItem(DEFAULT_REASON_CODE)))
            .andExpect(jsonPath("$.[*].reasonDescription").value(hasItem(DEFAULT_REASON_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].evidenceSubmittedAt").value(hasItem(DEFAULT_EVIDENCE_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())));

        // Check, that the count call also returns 1
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDisputeShouldNotBeFound(String filter) throws Exception {
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDisputeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDispute() throws Exception {
        // Get the dispute
        restDisputeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDispute() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute
        Dispute updatedDispute = disputeRepository.findById(dispute.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDispute are not directly saved in db
        em.detach(updatedDispute);
        updatedDispute
            .caseReference(UPDATED_CASE_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .reasonCode(UPDATED_REASON_CODE)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .evidenceSubmittedAt(UPDATED_EVIDENCE_SUBMITTED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT);
        DisputeDTO disputeDTO = disputeMapper.toDto(updatedDispute);

        restDisputeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disputeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisputeToMatchAllProperties(updatedDispute);
    }

    @Test
    @Transactional
    void putNonExistingDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disputeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disputeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisputeWithPatch() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute using partial update
        Dispute partialUpdatedDispute = new Dispute();
        partialUpdatedDispute.setId(dispute.getId());

        partialUpdatedDispute
            .currencyCode(UPDATED_CURRENCY_CODE)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .evidenceSubmittedAt(UPDATED_EVIDENCE_SUBMITTED_AT);

        restDisputeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDispute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDispute))
            )
            .andExpect(status().isOk());

        // Validate the Dispute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDispute, dispute), getPersistedDispute(dispute));
    }

    @Test
    @Transactional
    void fullUpdateDisputeWithPatch() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute using partial update
        Dispute partialUpdatedDispute = new Dispute();
        partialUpdatedDispute.setId(dispute.getId());

        partialUpdatedDispute
            .caseReference(UPDATED_CASE_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .reasonCode(UPDATED_REASON_CODE)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .evidenceSubmittedAt(UPDATED_EVIDENCE_SUBMITTED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT);

        restDisputeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDispute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDispute))
            )
            .andExpect(status().isOk());

        // Validate the Dispute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeUpdatableFieldsEquals(partialUpdatedDispute, getPersistedDispute(partialUpdatedDispute));
    }

    @Test
    @Transactional
    void patchNonExistingDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, disputeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disputeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disputeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(disputeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDispute() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.saveAndFlush(dispute);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dispute
        restDisputeMockMvc
            .perform(delete(ENTITY_API_URL_ID, dispute.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disputeRepository.count();
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

    protected Dispute getPersistedDispute(Dispute dispute) {
        return disputeRepository.findById(dispute.getId()).orElseThrow();
    }

    protected void assertPersistedDisputeToMatchAllProperties(Dispute expectedDispute) {
        assertDisputeAllPropertiesEquals(expectedDispute, getPersistedDispute(expectedDispute));
    }

    protected void assertPersistedDisputeToMatchUpdatableProperties(Dispute expectedDispute) {
        assertDisputeAllUpdatablePropertiesEquals(expectedDispute, getPersistedDispute(expectedDispute));
    }
}
