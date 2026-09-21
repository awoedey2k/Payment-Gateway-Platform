package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.TransactionAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.domain.enumeration.TransactionStatus;
import io.paymentgateway.core.repository.TransactionRepository;
import io.paymentgateway.core.service.dto.TransactionDTO;
import io.paymentgateway.core.service.mapper.TransactionMapper;
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
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_TENANT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_TENANT_REFERENCE = "BBBBBBBBBB";

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.AUTHORIZED;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_FEE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_FEE_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_FEE_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_NET_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_NET_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_NET_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_METHOD_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_METHOD_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_IDEMPOTENCY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_IDEMPOTENCY_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_PHONE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    private Transaction insertedTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .reference(DEFAULT_REFERENCE)
            .tenantReference(DEFAULT_TENANT_REFERENCE)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .feeAmount(DEFAULT_FEE_AMOUNT)
            .netAmount(DEFAULT_NET_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .paymentMethodCode(DEFAULT_PAYMENT_METHOD_CODE)
            .idempotencyKey(DEFAULT_IDEMPOTENCY_KEY)
            .customerEmail(DEFAULT_CUSTOMER_EMAIL)
            .customerPhone(DEFAULT_CUSTOMER_PHONE)
            .createdAt(DEFAULT_CREATED_AT)
            .completedAt(DEFAULT_COMPLETED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        transaction.setTenant(corporateTenant);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction updatedTransaction = new Transaction()
            .reference(UPDATED_REFERENCE)
            .tenantReference(UPDATED_TENANT_REFERENCE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .feeAmount(UPDATED_FEE_AMOUNT)
            .netAmount(UPDATED_NET_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .paymentMethodCode(UPDATED_PAYMENT_METHOD_CODE)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .customerEmail(UPDATED_CUSTOMER_EMAIL)
            .customerPhone(UPDATED_CUSTOMER_PHONE)
            .createdAt(UPDATED_CREATED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedTransaction.setTenant(corporateTenant);
        return updatedTransaction;
    }

    @BeforeEach
    void initTest() {
        transaction = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTransaction != null) {
            transactionRepository.delete(insertedTransaction);
            insertedTransaction = null;
        }
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        var returnedTransactionDTO = om.readValue(
            restTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionDTO.class
        );

        // Validate the Transaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransaction = transactionMapper.toEntity(returnedTransactionDTO);
        assertTransactionUpdatableFieldsEquals(returnedTransaction, getPersistedTransaction(returnedTransaction));

        insertedTransaction = returnedTransaction;
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setReference(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setStatus(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFeeAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setFeeAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNetAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setNetAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setCurrencyCode(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setCountryCode(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentMethodCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setPaymentMethodCode(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIdempotencyKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setIdempotencyKey(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setCreatedAt(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].tenantReference").value(hasItem(DEFAULT_TENANT_REFERENCE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].feeAmount").value(hasItem(sameNumber(DEFAULT_FEE_AMOUNT))))
            .andExpect(jsonPath("$.[*].netAmount").value(hasItem(sameNumber(DEFAULT_NET_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].paymentMethodCode").value(hasItem(DEFAULT_PAYMENT_METHOD_CODE)))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].customerEmail").value(hasItem(DEFAULT_CUSTOMER_EMAIL)))
            .andExpect(jsonPath("$.[*].customerPhone").value(hasItem(DEFAULT_CUSTOMER_PHONE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.tenantReference").value(DEFAULT_TENANT_REFERENCE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.feeAmount").value(sameNumber(DEFAULT_FEE_AMOUNT)))
            .andExpect(jsonPath("$.netAmount").value(sameNumber(DEFAULT_NET_AMOUNT)))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.paymentMethodCode").value(DEFAULT_PAYMENT_METHOD_CODE))
            .andExpect(jsonPath("$.idempotencyKey").value(DEFAULT_IDEMPOTENCY_KEY))
            .andExpect(jsonPath("$.customerEmail").value(DEFAULT_CUSTOMER_EMAIL))
            .andExpect(jsonPath("$.customerPhone").value(DEFAULT_CUSTOMER_PHONE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference equals to
        defaultTransactionFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference in
        defaultTransactionFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference is not null
        defaultTransactionFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference contains
        defaultTransactionFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference does not contain
        defaultTransactionFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where tenantReference equals to
        defaultTransactionFiltering(
            "tenantReference.equals=" + DEFAULT_TENANT_REFERENCE,
            "tenantReference.equals=" + UPDATED_TENANT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where tenantReference in
        defaultTransactionFiltering(
            "tenantReference.in=" + DEFAULT_TENANT_REFERENCE + "," + UPDATED_TENANT_REFERENCE,
            "tenantReference.in=" + UPDATED_TENANT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where tenantReference is not null
        defaultTransactionFiltering("tenantReference.specified=true", "tenantReference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where tenantReference contains
        defaultTransactionFiltering(
            "tenantReference.contains=" + DEFAULT_TENANT_REFERENCE,
            "tenantReference.contains=" + UPDATED_TENANT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where tenantReference does not contain
        defaultTransactionFiltering(
            "tenantReference.doesNotContain=" + UPDATED_TENANT_REFERENCE,
            "tenantReference.doesNotContain=" + DEFAULT_TENANT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status equals to
        defaultTransactionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status in
        defaultTransactionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status is not null
        defaultTransactionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount equals to
        defaultTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount in
        defaultTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is not null
        defaultTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than or equal to
        defaultTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than or equal to
        defaultTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than
        defaultTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than
        defaultTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount equals to
        defaultTransactionFiltering("feeAmount.equals=" + DEFAULT_FEE_AMOUNT, "feeAmount.equals=" + UPDATED_FEE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount in
        defaultTransactionFiltering("feeAmount.in=" + DEFAULT_FEE_AMOUNT + "," + UPDATED_FEE_AMOUNT, "feeAmount.in=" + UPDATED_FEE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount is not null
        defaultTransactionFiltering("feeAmount.specified=true", "feeAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount is greater than or equal to
        defaultTransactionFiltering(
            "feeAmount.greaterThanOrEqual=" + DEFAULT_FEE_AMOUNT,
            "feeAmount.greaterThanOrEqual=" + UPDATED_FEE_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount is less than or equal to
        defaultTransactionFiltering("feeAmount.lessThanOrEqual=" + DEFAULT_FEE_AMOUNT, "feeAmount.lessThanOrEqual=" + SMALLER_FEE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount is less than
        defaultTransactionFiltering("feeAmount.lessThan=" + UPDATED_FEE_AMOUNT, "feeAmount.lessThan=" + DEFAULT_FEE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFeeAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where feeAmount is greater than
        defaultTransactionFiltering("feeAmount.greaterThan=" + SMALLER_FEE_AMOUNT, "feeAmount.greaterThan=" + DEFAULT_FEE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount equals to
        defaultTransactionFiltering("netAmount.equals=" + DEFAULT_NET_AMOUNT, "netAmount.equals=" + UPDATED_NET_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount in
        defaultTransactionFiltering("netAmount.in=" + DEFAULT_NET_AMOUNT + "," + UPDATED_NET_AMOUNT, "netAmount.in=" + UPDATED_NET_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount is not null
        defaultTransactionFiltering("netAmount.specified=true", "netAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount is greater than or equal to
        defaultTransactionFiltering(
            "netAmount.greaterThanOrEqual=" + DEFAULT_NET_AMOUNT,
            "netAmount.greaterThanOrEqual=" + UPDATED_NET_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount is less than or equal to
        defaultTransactionFiltering("netAmount.lessThanOrEqual=" + DEFAULT_NET_AMOUNT, "netAmount.lessThanOrEqual=" + SMALLER_NET_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount is less than
        defaultTransactionFiltering("netAmount.lessThan=" + UPDATED_NET_AMOUNT, "netAmount.lessThan=" + DEFAULT_NET_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByNetAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where netAmount is greater than
        defaultTransactionFiltering("netAmount.greaterThan=" + SMALLER_NET_AMOUNT, "netAmount.greaterThan=" + DEFAULT_NET_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where currencyCode equals to
        defaultTransactionFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where currencyCode in
        defaultTransactionFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where currencyCode is not null
        defaultTransactionFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where currencyCode contains
        defaultTransactionFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where currencyCode does not contain
        defaultTransactionFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where countryCode equals to
        defaultTransactionFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where countryCode in
        defaultTransactionFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where countryCode is not null
        defaultTransactionFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where countryCode contains
        defaultTransactionFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where countryCode does not contain
        defaultTransactionFiltering(
            "countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE,
            "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentMethodCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentMethodCode equals to
        defaultTransactionFiltering(
            "paymentMethodCode.equals=" + DEFAULT_PAYMENT_METHOD_CODE,
            "paymentMethodCode.equals=" + UPDATED_PAYMENT_METHOD_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentMethodCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentMethodCode in
        defaultTransactionFiltering(
            "paymentMethodCode.in=" + DEFAULT_PAYMENT_METHOD_CODE + "," + UPDATED_PAYMENT_METHOD_CODE,
            "paymentMethodCode.in=" + UPDATED_PAYMENT_METHOD_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentMethodCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentMethodCode is not null
        defaultTransactionFiltering("paymentMethodCode.specified=true", "paymentMethodCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentMethodCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentMethodCode contains
        defaultTransactionFiltering(
            "paymentMethodCode.contains=" + DEFAULT_PAYMENT_METHOD_CODE,
            "paymentMethodCode.contains=" + UPDATED_PAYMENT_METHOD_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentMethodCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentMethodCode does not contain
        defaultTransactionFiltering(
            "paymentMethodCode.doesNotContain=" + UPDATED_PAYMENT_METHOD_CODE,
            "paymentMethodCode.doesNotContain=" + DEFAULT_PAYMENT_METHOD_CODE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByIdempotencyKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where idempotencyKey equals to
        defaultTransactionFiltering("idempotencyKey.equals=" + DEFAULT_IDEMPOTENCY_KEY, "idempotencyKey.equals=" + UPDATED_IDEMPOTENCY_KEY);
    }

    @Test
    @Transactional
    void getAllTransactionsByIdempotencyKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where idempotencyKey in
        defaultTransactionFiltering(
            "idempotencyKey.in=" + DEFAULT_IDEMPOTENCY_KEY + "," + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.in=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByIdempotencyKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where idempotencyKey is not null
        defaultTransactionFiltering("idempotencyKey.specified=true", "idempotencyKey.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByIdempotencyKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where idempotencyKey contains
        defaultTransactionFiltering(
            "idempotencyKey.contains=" + DEFAULT_IDEMPOTENCY_KEY,
            "idempotencyKey.contains=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByIdempotencyKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where idempotencyKey does not contain
        defaultTransactionFiltering(
            "idempotencyKey.doesNotContain=" + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.doesNotContain=" + DEFAULT_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerEmail equals to
        defaultTransactionFiltering("customerEmail.equals=" + DEFAULT_CUSTOMER_EMAIL, "customerEmail.equals=" + UPDATED_CUSTOMER_EMAIL);
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerEmail in
        defaultTransactionFiltering(
            "customerEmail.in=" + DEFAULT_CUSTOMER_EMAIL + "," + UPDATED_CUSTOMER_EMAIL,
            "customerEmail.in=" + UPDATED_CUSTOMER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerEmail is not null
        defaultTransactionFiltering("customerEmail.specified=true", "customerEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerEmail contains
        defaultTransactionFiltering("customerEmail.contains=" + DEFAULT_CUSTOMER_EMAIL, "customerEmail.contains=" + UPDATED_CUSTOMER_EMAIL);
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerEmail does not contain
        defaultTransactionFiltering(
            "customerEmail.doesNotContain=" + UPDATED_CUSTOMER_EMAIL,
            "customerEmail.doesNotContain=" + DEFAULT_CUSTOMER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerPhone equals to
        defaultTransactionFiltering("customerPhone.equals=" + DEFAULT_CUSTOMER_PHONE, "customerPhone.equals=" + UPDATED_CUSTOMER_PHONE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerPhone in
        defaultTransactionFiltering(
            "customerPhone.in=" + DEFAULT_CUSTOMER_PHONE + "," + UPDATED_CUSTOMER_PHONE,
            "customerPhone.in=" + UPDATED_CUSTOMER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerPhone is not null
        defaultTransactionFiltering("customerPhone.specified=true", "customerPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerPhone contains
        defaultTransactionFiltering("customerPhone.contains=" + DEFAULT_CUSTOMER_PHONE, "customerPhone.contains=" + UPDATED_CUSTOMER_PHONE);
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where customerPhone does not contain
        defaultTransactionFiltering(
            "customerPhone.doesNotContain=" + UPDATED_CUSTOMER_PHONE,
            "customerPhone.doesNotContain=" + DEFAULT_CUSTOMER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt equals to
        defaultTransactionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt in
        defaultTransactionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is not null
        defaultTransactionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where completedAt equals to
        defaultTransactionFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where completedAt in
        defaultTransactionFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where completedAt is not null
        defaultTransactionFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            transactionRepository.saveAndFlush(transaction);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        transaction.setTenant(tenant);
        transactionRepository.saveAndFlush(transaction);
        Long tenantId = tenant.getId();
        // Get all the transactionList where tenant equals to tenantId
        defaultTransactionShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the transactionList where tenant equals to (tenantId + 1)
        defaultTransactionShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTransactionShouldBeFound(shouldBeFound);
        defaultTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].tenantReference").value(hasItem(DEFAULT_TENANT_REFERENCE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].feeAmount").value(hasItem(sameNumber(DEFAULT_FEE_AMOUNT))))
            .andExpect(jsonPath("$.[*].netAmount").value(hasItem(sameNumber(DEFAULT_NET_AMOUNT))))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].paymentMethodCode").value(hasItem(DEFAULT_PAYMENT_METHOD_CODE)))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].customerEmail").value(hasItem(DEFAULT_CUSTOMER_EMAIL)))
            .andExpect(jsonPath("$.[*].customerPhone").value(hasItem(DEFAULT_CUSTOMER_PHONE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .reference(UPDATED_REFERENCE)
            .tenantReference(UPDATED_TENANT_REFERENCE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .feeAmount(UPDATED_FEE_AMOUNT)
            .netAmount(UPDATED_NET_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .paymentMethodCode(UPDATED_PAYMENT_METHOD_CODE)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .customerEmail(UPDATED_CUSTOMER_EMAIL)
            .customerPhone(UPDATED_CUSTOMER_PHONE)
            .createdAt(UPDATED_CREATED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionToMatchAllProperties(updatedTransaction);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .tenantReference(UPDATED_TENANT_REFERENCE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .countryCode(UPDATED_COUNTRY_CODE)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .customerEmail(UPDATED_CUSTOMER_EMAIL)
            .completedAt(UPDATED_COMPLETED_AT);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransaction, transaction),
            getPersistedTransaction(transaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .reference(UPDATED_REFERENCE)
            .tenantReference(UPDATED_TENANT_REFERENCE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .feeAmount(UPDATED_FEE_AMOUNT)
            .netAmount(UPDATED_NET_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .paymentMethodCode(UPDATED_PAYMENT_METHOD_CODE)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .customerEmail(UPDATED_CUSTOMER_EMAIL)
            .customerPhone(UPDATED_CUSTOMER_PHONE)
            .createdAt(UPDATED_CREATED_AT)
            .completedAt(UPDATED_COMPLETED_AT);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(partialUpdatedTransaction, getPersistedTransaction(partialUpdatedTransaction));
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transactionRepository.count();
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

    protected Transaction getPersistedTransaction(Transaction transaction) {
        return transactionRepository.findById(transaction.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionToMatchAllProperties(Transaction expectedTransaction) {
        assertTransactionAllPropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }

    protected void assertPersistedTransactionToMatchUpdatableProperties(Transaction expectedTransaction) {
        assertTransactionAllUpdatablePropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }
}
