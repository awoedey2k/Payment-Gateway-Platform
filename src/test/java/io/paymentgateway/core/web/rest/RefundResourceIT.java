package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.RefundAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.Refund;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.domain.enumeration.RefundReason;
import io.paymentgateway.core.domain.enumeration.RefundStatus;
import io.paymentgateway.core.repository.RefundRepository;
import io.paymentgateway.core.service.dto.RefundDTO;
import io.paymentgateway.core.service.mapper.RefundMapper;
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
 * Integration tests for the {@link RefundResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RefundResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final RefundReason DEFAULT_REASON = RefundReason.CUSTOMER_REQUEST_RETURN;
    private static final RefundReason UPDATED_REASON = RefundReason.FRAUD;

    private static final RefundStatus DEFAULT_STATUS = RefundStatus.PENDING;
    private static final RefundStatus UPDATED_STATUS = RefundStatus.REFUNDED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/refunds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRefundMockMvc;

    private Refund refund;

    private Refund insertedRefund;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createEntity(EntityManager em) {
        Refund refund = new Refund()
            .reference(DEFAULT_REFERENCE)
            .amount(DEFAULT_AMOUNT)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            transaction = TransactionResourceIT.createEntity(em);
            em.persist(transaction);
            em.flush();
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        refund.setTransaction(transaction);
        return refund;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createUpdatedEntity(EntityManager em) {
        Refund updatedRefund = new Refund()
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            transaction = TransactionResourceIT.createUpdatedEntity(em);
            em.persist(transaction);
            em.flush();
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        updatedRefund.setTransaction(transaction);
        return updatedRefund;
    }

    @BeforeEach
    void initTest() {
        refund = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRefund != null) {
            refundRepository.delete(insertedRefund);
            insertedRefund = null;
        }
    }

    @Test
    @Transactional
    void createRefund() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);
        var returnedRefundDTO = om.readValue(
            restRefundMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RefundDTO.class
        );

        // Validate the Refund in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRefund = refundMapper.toEntity(returnedRefundDTO);
        assertRefundUpdatableFieldsEquals(returnedRefund, getPersistedRefund(returnedRefund));

        insertedRefund = returnedRefund;
    }

    @Test
    @Transactional
    void createRefundWithExistingId() throws Exception {
        // Create the Refund with an existing ID
        refund.setId(1L);
        RefundDTO refundDTO = refundMapper.toDto(refund);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setReference(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setAmount(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setReason(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setStatus(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        refund.setCreatedAt(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRefunds() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get the refund
        restRefundMockMvc
            .perform(get(ENTITY_API_URL_ID, refund.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(refund.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getRefundsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        Long id = refund.getId();

        defaultRefundFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRefundFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRefundFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference equals to
        defaultRefundFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference in
        defaultRefundFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference is not null
        defaultRefundFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference contains
        defaultRefundFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference does not contain
        defaultRefundFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount equals to
        defaultRefundFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount in
        defaultRefundFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is not null
        defaultRefundFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than or equal to
        defaultRefundFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than or equal to
        defaultRefundFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than
        defaultRefundFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than
        defaultRefundFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reason equals to
        defaultRefundFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllRefundsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reason in
        defaultRefundFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllRefundsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reason is not null
        defaultRefundFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status equals to
        defaultRefundFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status in
        defaultRefundFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where status is not null
        defaultRefundFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt equals to
        defaultRefundFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt in
        defaultRefundFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRefundsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where createdAt is not null
        defaultRefundFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionIsEqualToSomething() throws Exception {
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            transaction = TransactionResourceIT.createEntity(em);
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).getFirst();
        }
        em.persist(transaction);
        em.flush();
        refund.setTransaction(transaction);
        refundRepository.saveAndFlush(refund);
        Long transactionId = transaction.getId();
        // Get all the refundList where transaction equals to transactionId
        defaultRefundShouldBeFound("transactionId.equals=" + transactionId);

        // Get all the refundList where transaction equals to (transactionId + 1)
        defaultRefundShouldNotBeFound("transactionId.equals=" + (transactionId + 1));
    }

    private void defaultRefundFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRefundShouldBeFound(shouldBeFound);
        defaultRefundShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRefundShouldBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRefundShouldNotBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRefund() throws Exception {
        // Get the refund
        restRefundMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund
        Refund updatedRefund = refundRepository.findById(refund.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRefund are not directly saved in db
        em.detach(updatedRefund);
        updatedRefund
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        RefundDTO refundDTO = refundMapper.toDto(updatedRefund);

        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRefundToMatchAllProperties(updatedRefund);
    }

    @Test
    @Transactional
    void putNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund.reference(UPDATED_REFERENCE).reason(UPDATED_REASON).status(UPDATED_STATUS);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRefund, refund), getPersistedRefund(refund));
    }

    @Test
    @Transactional
    void fullUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(partialUpdatedRefund, getPersistedRefund(partialUpdatedRefund));
    }

    @Test
    @Transactional
    void patchNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, refundDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the refund
        restRefundMockMvc
            .perform(delete(ENTITY_API_URL_ID, refund.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return refundRepository.count();
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

    protected Refund getPersistedRefund(Refund refund) {
        return refundRepository.findById(refund.getId()).orElseThrow();
    }

    protected void assertPersistedRefundToMatchAllProperties(Refund expectedRefund) {
        assertRefundAllPropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }

    protected void assertPersistedRefundToMatchUpdatableProperties(Refund expectedRefund) {
        assertRefundAllUpdatablePropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }
}
