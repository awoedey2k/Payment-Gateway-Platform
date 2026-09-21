package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.LedgerAccountAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.domain.enumeration.LedgerAccountType;
import io.paymentgateway.core.repository.LedgerAccountRepository;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import io.paymentgateway.core.service.mapper.LedgerAccountMapper;
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
 * Integration tests for the {@link LedgerAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LedgerAccountResourceIT {

    private static final String DEFAULT_ACCOUNT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_CODE = "BBBBBBBBBB";

    private static final LedgerAccountType DEFAULT_ACCOUNT_TYPE = LedgerAccountType.ASSET;
    private static final LedgerAccountType UPDATED_ACCOUNT_TYPE = LedgerAccountType.LIABILITY;

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ledger-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LedgerAccountRepository ledgerAccountRepository;

    @Autowired
    private LedgerAccountMapper ledgerAccountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLedgerAccountMockMvc;

    private LedgerAccount ledgerAccount;

    private LedgerAccount insertedLedgerAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LedgerAccount createEntity() {
        return new LedgerAccount().accountCode(DEFAULT_ACCOUNT_CODE).accountType(DEFAULT_ACCOUNT_TYPE).currencyCode(DEFAULT_CURRENCY_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LedgerAccount createUpdatedEntity() {
        return new LedgerAccount().accountCode(UPDATED_ACCOUNT_CODE).accountType(UPDATED_ACCOUNT_TYPE).currencyCode(UPDATED_CURRENCY_CODE);
    }

    @BeforeEach
    void initTest() {
        ledgerAccount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLedgerAccount != null) {
            ledgerAccountRepository.delete(insertedLedgerAccount);
            insertedLedgerAccount = null;
        }
    }

    @Test
    @Transactional
    void createLedgerAccount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);
        var returnedLedgerAccountDTO = om.readValue(
            restLedgerAccountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LedgerAccountDTO.class
        );

        // Validate the LedgerAccount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLedgerAccount = ledgerAccountMapper.toEntity(returnedLedgerAccountDTO);
        assertLedgerAccountUpdatableFieldsEquals(returnedLedgerAccount, getPersistedLedgerAccount(returnedLedgerAccount));

        insertedLedgerAccount = returnedLedgerAccount;
    }

    @Test
    @Transactional
    void createLedgerAccountWithExistingId() throws Exception {
        // Create the LedgerAccount with an existing ID
        ledgerAccount.setId(1L);
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLedgerAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAccountCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ledgerAccount.setAccountCode(null);

        // Create the LedgerAccount, which fails.
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        restLedgerAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ledgerAccount.setAccountType(null);

        // Create the LedgerAccount, which fails.
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        restLedgerAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ledgerAccount.setCurrencyCode(null);

        // Create the LedgerAccount, which fails.
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        restLedgerAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLedgerAccounts() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ledgerAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountCode").value(hasItem(DEFAULT_ACCOUNT_CODE)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)));
    }

    @Test
    @Transactional
    void getLedgerAccount() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get the ledgerAccount
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, ledgerAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ledgerAccount.getId().intValue()))
            .andExpect(jsonPath("$.accountCode").value(DEFAULT_ACCOUNT_CODE))
            .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE));
    }

    @Test
    @Transactional
    void getLedgerAccountsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        Long id = ledgerAccount.getId();

        defaultLedgerAccountFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLedgerAccountFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLedgerAccountFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountCode equals to
        defaultLedgerAccountFiltering("accountCode.equals=" + DEFAULT_ACCOUNT_CODE, "accountCode.equals=" + UPDATED_ACCOUNT_CODE);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountCode in
        defaultLedgerAccountFiltering(
            "accountCode.in=" + DEFAULT_ACCOUNT_CODE + "," + UPDATED_ACCOUNT_CODE,
            "accountCode.in=" + UPDATED_ACCOUNT_CODE
        );
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountCode is not null
        defaultLedgerAccountFiltering("accountCode.specified=true", "accountCode.specified=false");
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountCode contains
        defaultLedgerAccountFiltering("accountCode.contains=" + DEFAULT_ACCOUNT_CODE, "accountCode.contains=" + UPDATED_ACCOUNT_CODE);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountCode does not contain
        defaultLedgerAccountFiltering(
            "accountCode.doesNotContain=" + UPDATED_ACCOUNT_CODE,
            "accountCode.doesNotContain=" + DEFAULT_ACCOUNT_CODE
        );
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountType equals to
        defaultLedgerAccountFiltering("accountType.equals=" + DEFAULT_ACCOUNT_TYPE, "accountType.equals=" + UPDATED_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountType in
        defaultLedgerAccountFiltering(
            "accountType.in=" + DEFAULT_ACCOUNT_TYPE + "," + UPDATED_ACCOUNT_TYPE,
            "accountType.in=" + UPDATED_ACCOUNT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByAccountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where accountType is not null
        defaultLedgerAccountFiltering("accountType.specified=true", "accountType.specified=false");
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where currencyCode equals to
        defaultLedgerAccountFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where currencyCode in
        defaultLedgerAccountFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where currencyCode is not null
        defaultLedgerAccountFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where currencyCode contains
        defaultLedgerAccountFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllLedgerAccountsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList where currencyCode does not contain
        defaultLedgerAccountFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    private void defaultLedgerAccountFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLedgerAccountShouldBeFound(shouldBeFound);
        defaultLedgerAccountShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLedgerAccountShouldBeFound(String filter) throws Exception {
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ledgerAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountCode").value(hasItem(DEFAULT_ACCOUNT_CODE)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)));

        // Check, that the count call also returns 1
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLedgerAccountShouldNotBeFound(String filter) throws Exception {
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLedgerAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLedgerAccount() throws Exception {
        // Get the ledgerAccount
        restLedgerAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLedgerAccount() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ledgerAccount
        LedgerAccount updatedLedgerAccount = ledgerAccountRepository.findById(ledgerAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLedgerAccount are not directly saved in db
        em.detach(updatedLedgerAccount);
        updatedLedgerAccount.accountCode(UPDATED_ACCOUNT_CODE).accountType(UPDATED_ACCOUNT_TYPE).currencyCode(UPDATED_CURRENCY_CODE);
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(updatedLedgerAccount);

        restLedgerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ledgerAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ledgerAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLedgerAccountToMatchAllProperties(updatedLedgerAccount);
    }

    @Test
    @Transactional
    void putNonExistingLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ledgerAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ledgerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ledgerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLedgerAccountWithPatch() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ledgerAccount using partial update
        LedgerAccount partialUpdatedLedgerAccount = new LedgerAccount();
        partialUpdatedLedgerAccount.setId(ledgerAccount.getId());

        partialUpdatedLedgerAccount.accountCode(UPDATED_ACCOUNT_CODE).accountType(UPDATED_ACCOUNT_TYPE).currencyCode(UPDATED_CURRENCY_CODE);

        restLedgerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLedgerAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLedgerAccount))
            )
            .andExpect(status().isOk());

        // Validate the LedgerAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLedgerAccountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLedgerAccount, ledgerAccount),
            getPersistedLedgerAccount(ledgerAccount)
        );
    }

    @Test
    @Transactional
    void fullUpdateLedgerAccountWithPatch() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ledgerAccount using partial update
        LedgerAccount partialUpdatedLedgerAccount = new LedgerAccount();
        partialUpdatedLedgerAccount.setId(ledgerAccount.getId());

        partialUpdatedLedgerAccount.accountCode(UPDATED_ACCOUNT_CODE).accountType(UPDATED_ACCOUNT_TYPE).currencyCode(UPDATED_CURRENCY_CODE);

        restLedgerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLedgerAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLedgerAccount))
            )
            .andExpect(status().isOk());

        // Validate the LedgerAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLedgerAccountUpdatableFieldsEquals(partialUpdatedLedgerAccount, getPersistedLedgerAccount(partialUpdatedLedgerAccount));
    }

    @Test
    @Transactional
    void patchNonExistingLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ledgerAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ledgerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ledgerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLedgerAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ledgerAccount.setId(longCount.incrementAndGet());

        // Create the LedgerAccount
        LedgerAccountDTO ledgerAccountDTO = ledgerAccountMapper.toDto(ledgerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerAccountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ledgerAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LedgerAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLedgerAccount() throws Exception {
        // Initialize the database
        insertedLedgerAccount = ledgerAccountRepository.saveAndFlush(ledgerAccount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ledgerAccount
        restLedgerAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, ledgerAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ledgerAccountRepository.count();
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

    protected LedgerAccount getPersistedLedgerAccount(LedgerAccount ledgerAccount) {
        return ledgerAccountRepository.findById(ledgerAccount.getId()).orElseThrow();
    }

    protected void assertPersistedLedgerAccountToMatchAllProperties(LedgerAccount expectedLedgerAccount) {
        assertLedgerAccountAllPropertiesEquals(expectedLedgerAccount, getPersistedLedgerAccount(expectedLedgerAccount));
    }

    protected void assertPersistedLedgerAccountToMatchUpdatableProperties(LedgerAccount expectedLedgerAccount) {
        assertLedgerAccountAllUpdatablePropertiesEquals(expectedLedgerAccount, getPersistedLedgerAccount(expectedLedgerAccount));
    }
}
