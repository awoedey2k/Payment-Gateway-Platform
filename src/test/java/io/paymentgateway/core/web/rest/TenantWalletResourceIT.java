package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.TenantWalletAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantWallet;
import io.paymentgateway.core.repository.TenantWalletRepository;
import io.paymentgateway.core.service.dto.TenantWalletDTO;
import io.paymentgateway.core.service.mapper.TenantWalletMapper;
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
 * Integration tests for the {@link TenantWalletResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantWalletResourceIT {

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AVAILABLE_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVAILABLE_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_AVAILABLE_BALANCE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LOCKED_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LOCKED_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LOCKED_BALANCE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/tenant-wallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TenantWalletRepository tenantWalletRepository;

    @Autowired
    private TenantWalletMapper tenantWalletMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantWalletMockMvc;

    private TenantWallet tenantWallet;

    private TenantWallet insertedTenantWallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantWallet createEntity(EntityManager em) {
        TenantWallet tenantWallet = new TenantWallet()
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .availableBalance(DEFAULT_AVAILABLE_BALANCE)
            .lockedBalance(DEFAULT_LOCKED_BALANCE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        tenantWallet.setTenant(corporateTenant);
        return tenantWallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TenantWallet createUpdatedEntity(EntityManager em) {
        TenantWallet updatedTenantWallet = new TenantWallet()
            .currencyCode(UPDATED_CURRENCY_CODE)
            .availableBalance(UPDATED_AVAILABLE_BALANCE)
            .lockedBalance(UPDATED_LOCKED_BALANCE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedTenantWallet.setTenant(corporateTenant);
        return updatedTenantWallet;
    }

    @BeforeEach
    void initTest() {
        tenantWallet = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTenantWallet != null) {
            tenantWalletRepository.delete(insertedTenantWallet);
            insertedTenantWallet = null;
        }
    }

    @Test
    @Transactional
    void createTenantWallet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);
        var returnedTenantWalletDTO = om.readValue(
            restTenantWalletMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TenantWalletDTO.class
        );

        // Validate the TenantWallet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTenantWallet = tenantWalletMapper.toEntity(returnedTenantWalletDTO);
        assertTenantWalletUpdatableFieldsEquals(returnedTenantWallet, getPersistedTenantWallet(returnedTenantWallet));

        insertedTenantWallet = returnedTenantWallet;
    }

    @Test
    @Transactional
    void createTenantWalletWithExistingId() throws Exception {
        // Create the TenantWallet with an existing ID
        tenantWallet.setId(1L);
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantWalletMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantWallet.setCurrencyCode(null);

        // Create the TenantWallet, which fails.
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        restTenantWalletMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAvailableBalanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantWallet.setAvailableBalance(null);

        // Create the TenantWallet, which fails.
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        restTenantWalletMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLockedBalanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tenantWallet.setLockedBalance(null);

        // Create the TenantWallet, which fails.
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        restTenantWalletMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenantWallets() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantWallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].availableBalance").value(hasItem(sameNumber(DEFAULT_AVAILABLE_BALANCE))))
            .andExpect(jsonPath("$.[*].lockedBalance").value(hasItem(sameNumber(DEFAULT_LOCKED_BALANCE))));
    }

    @Test
    @Transactional
    void getTenantWallet() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get the tenantWallet
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL_ID, tenantWallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenantWallet.getId().intValue()))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.availableBalance").value(sameNumber(DEFAULT_AVAILABLE_BALANCE)))
            .andExpect(jsonPath("$.lockedBalance").value(sameNumber(DEFAULT_LOCKED_BALANCE)));
    }

    @Test
    @Transactional
    void getTenantWalletsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        Long id = tenantWallet.getId();

        defaultTenantWalletFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTenantWalletFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTenantWalletFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTenantWalletsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where currencyCode equals to
        defaultTenantWalletFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllTenantWalletsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where currencyCode in
        defaultTenantWalletFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where currencyCode is not null
        defaultTenantWalletFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantWalletsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where currencyCode contains
        defaultTenantWalletFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllTenantWalletsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where currencyCode does not contain
        defaultTenantWalletFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance equals to
        defaultTenantWalletFiltering(
            "availableBalance.equals=" + DEFAULT_AVAILABLE_BALANCE,
            "availableBalance.equals=" + UPDATED_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance in
        defaultTenantWalletFiltering(
            "availableBalance.in=" + DEFAULT_AVAILABLE_BALANCE + "," + UPDATED_AVAILABLE_BALANCE,
            "availableBalance.in=" + UPDATED_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance is not null
        defaultTenantWalletFiltering("availableBalance.specified=true", "availableBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance is greater than or equal to
        defaultTenantWalletFiltering(
            "availableBalance.greaterThanOrEqual=" + DEFAULT_AVAILABLE_BALANCE,
            "availableBalance.greaterThanOrEqual=" + UPDATED_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance is less than or equal to
        defaultTenantWalletFiltering(
            "availableBalance.lessThanOrEqual=" + DEFAULT_AVAILABLE_BALANCE,
            "availableBalance.lessThanOrEqual=" + SMALLER_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance is less than
        defaultTenantWalletFiltering(
            "availableBalance.lessThan=" + UPDATED_AVAILABLE_BALANCE,
            "availableBalance.lessThan=" + DEFAULT_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByAvailableBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where availableBalance is greater than
        defaultTenantWalletFiltering(
            "availableBalance.greaterThan=" + SMALLER_AVAILABLE_BALANCE,
            "availableBalance.greaterThan=" + DEFAULT_AVAILABLE_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance equals to
        defaultTenantWalletFiltering("lockedBalance.equals=" + DEFAULT_LOCKED_BALANCE, "lockedBalance.equals=" + UPDATED_LOCKED_BALANCE);
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance in
        defaultTenantWalletFiltering(
            "lockedBalance.in=" + DEFAULT_LOCKED_BALANCE + "," + UPDATED_LOCKED_BALANCE,
            "lockedBalance.in=" + UPDATED_LOCKED_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance is not null
        defaultTenantWalletFiltering("lockedBalance.specified=true", "lockedBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance is greater than or equal to
        defaultTenantWalletFiltering(
            "lockedBalance.greaterThanOrEqual=" + DEFAULT_LOCKED_BALANCE,
            "lockedBalance.greaterThanOrEqual=" + UPDATED_LOCKED_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance is less than or equal to
        defaultTenantWalletFiltering(
            "lockedBalance.lessThanOrEqual=" + DEFAULT_LOCKED_BALANCE,
            "lockedBalance.lessThanOrEqual=" + SMALLER_LOCKED_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance is less than
        defaultTenantWalletFiltering(
            "lockedBalance.lessThan=" + UPDATED_LOCKED_BALANCE,
            "lockedBalance.lessThan=" + DEFAULT_LOCKED_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByLockedBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        // Get all the tenantWalletList where lockedBalance is greater than
        defaultTenantWalletFiltering(
            "lockedBalance.greaterThan=" + SMALLER_LOCKED_BALANCE,
            "lockedBalance.greaterThan=" + DEFAULT_LOCKED_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllTenantWalletsByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            tenantWalletRepository.saveAndFlush(tenantWallet);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        tenantWallet.setTenant(tenant);
        tenantWalletRepository.saveAndFlush(tenantWallet);
        Long tenantId = tenant.getId();
        // Get all the tenantWalletList where tenant equals to tenantId
        defaultTenantWalletShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the tenantWalletList where tenant equals to (tenantId + 1)
        defaultTenantWalletShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultTenantWalletFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTenantWalletShouldBeFound(shouldBeFound);
        defaultTenantWalletShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTenantWalletShouldBeFound(String filter) throws Exception {
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenantWallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].availableBalance").value(hasItem(sameNumber(DEFAULT_AVAILABLE_BALANCE))))
            .andExpect(jsonPath("$.[*].lockedBalance").value(hasItem(sameNumber(DEFAULT_LOCKED_BALANCE))));

        // Check, that the count call also returns 1
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTenantWalletShouldNotBeFound(String filter) throws Exception {
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTenantWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTenantWallet() throws Exception {
        // Get the tenantWallet
        restTenantWalletMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTenantWallet() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantWallet
        TenantWallet updatedTenantWallet = tenantWalletRepository.findById(tenantWallet.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTenantWallet are not directly saved in db
        em.detach(updatedTenantWallet);
        updatedTenantWallet
            .currencyCode(UPDATED_CURRENCY_CODE)
            .availableBalance(UPDATED_AVAILABLE_BALANCE)
            .lockedBalance(UPDATED_LOCKED_BALANCE);
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(updatedTenantWallet);

        restTenantWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantWalletDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantWalletDTO))
            )
            .andExpect(status().isOk());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTenantWalletToMatchAllProperties(updatedTenantWallet);
    }

    @Test
    @Transactional
    void putNonExistingTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantWalletDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantWalletWithPatch() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantWallet using partial update
        TenantWallet partialUpdatedTenantWallet = new TenantWallet();
        partialUpdatedTenantWallet.setId(tenantWallet.getId());

        partialUpdatedTenantWallet.currencyCode(UPDATED_CURRENCY_CODE).lockedBalance(UPDATED_LOCKED_BALANCE);

        restTenantWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantWallet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantWallet))
            )
            .andExpect(status().isOk());

        // Validate the TenantWallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantWalletUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTenantWallet, tenantWallet),
            getPersistedTenantWallet(tenantWallet)
        );
    }

    @Test
    @Transactional
    void fullUpdateTenantWalletWithPatch() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenantWallet using partial update
        TenantWallet partialUpdatedTenantWallet = new TenantWallet();
        partialUpdatedTenantWallet.setId(tenantWallet.getId());

        partialUpdatedTenantWallet
            .currencyCode(UPDATED_CURRENCY_CODE)
            .availableBalance(UPDATED_AVAILABLE_BALANCE)
            .lockedBalance(UPDATED_LOCKED_BALANCE);

        restTenantWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenantWallet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenantWallet))
            )
            .andExpect(status().isOk());

        // Validate the TenantWallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantWalletUpdatableFieldsEquals(partialUpdatedTenantWallet, getPersistedTenantWallet(partialUpdatedTenantWallet));
    }

    @Test
    @Transactional
    void patchNonExistingTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantWalletDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantWalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenantWallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantWallet.setId(longCount.incrementAndGet());

        // Create the TenantWallet
        TenantWalletDTO tenantWalletDTO = tenantWalletMapper.toDto(tenantWallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantWalletMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tenantWalletDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TenantWallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenantWallet() throws Exception {
        // Initialize the database
        insertedTenantWallet = tenantWalletRepository.saveAndFlush(tenantWallet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tenantWallet
        restTenantWalletMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenantWallet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tenantWalletRepository.count();
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

    protected TenantWallet getPersistedTenantWallet(TenantWallet tenantWallet) {
        return tenantWalletRepository.findById(tenantWallet.getId()).orElseThrow();
    }

    protected void assertPersistedTenantWalletToMatchAllProperties(TenantWallet expectedTenantWallet) {
        assertTenantWalletAllPropertiesEquals(expectedTenantWallet, getPersistedTenantWallet(expectedTenantWallet));
    }

    protected void assertPersistedTenantWalletToMatchUpdatableProperties(TenantWallet expectedTenantWallet) {
        assertTenantWalletAllUpdatablePropertiesEquals(expectedTenantWallet, getPersistedTenantWallet(expectedTenantWallet));
    }
}
