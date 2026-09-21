package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.JournalLineAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.domain.JournalLine;
import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.repository.JournalLineRepository;
import io.paymentgateway.core.service.dto.JournalLineDTO;
import io.paymentgateway.core.service.mapper.JournalLineMapper;
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
 * Integration tests for the {@link JournalLineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JournalLineResourceIT {

    private static final BigDecimal DEFAULT_DEBIT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DEBIT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DEBIT_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_CREDIT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CREDIT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_CREDIT_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/journal-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JournalLineRepository journalLineRepository;

    @Autowired
    private JournalLineMapper journalLineMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJournalLineMockMvc;

    private JournalLine journalLine;

    private JournalLine insertedJournalLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalLine createEntity(EntityManager em) {
        JournalLine journalLine = new JournalLine().debitAmount(DEFAULT_DEBIT_AMOUNT).creditAmount(DEFAULT_CREDIT_AMOUNT);
        // Add required entity
        LedgerAccount ledgerAccount;
        if (TestUtil.findAll(em, LedgerAccount.class).isEmpty()) {
            ledgerAccount = LedgerAccountResourceIT.createEntity();
            em.persist(ledgerAccount);
            em.flush();
        } else {
            ledgerAccount = TestUtil.findAll(em, LedgerAccount.class).getFirst();
        }
        journalLine.setAccount(ledgerAccount);
        // Add required entity
        JournalEntry journalEntry;
        if (TestUtil.findAll(em, JournalEntry.class).isEmpty()) {
            journalEntry = JournalEntryResourceIT.createEntity();
            em.persist(journalEntry);
            em.flush();
        } else {
            journalEntry = TestUtil.findAll(em, JournalEntry.class).getFirst();
        }
        journalLine.setJournalEntry(journalEntry);
        return journalLine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalLine createUpdatedEntity(EntityManager em) {
        JournalLine updatedJournalLine = new JournalLine().debitAmount(UPDATED_DEBIT_AMOUNT).creditAmount(UPDATED_CREDIT_AMOUNT);
        // Add required entity
        LedgerAccount ledgerAccount;
        if (TestUtil.findAll(em, LedgerAccount.class).isEmpty()) {
            ledgerAccount = LedgerAccountResourceIT.createUpdatedEntity();
            em.persist(ledgerAccount);
            em.flush();
        } else {
            ledgerAccount = TestUtil.findAll(em, LedgerAccount.class).getFirst();
        }
        updatedJournalLine.setAccount(ledgerAccount);
        // Add required entity
        JournalEntry journalEntry;
        if (TestUtil.findAll(em, JournalEntry.class).isEmpty()) {
            journalEntry = JournalEntryResourceIT.createUpdatedEntity();
            em.persist(journalEntry);
            em.flush();
        } else {
            journalEntry = TestUtil.findAll(em, JournalEntry.class).getFirst();
        }
        updatedJournalLine.setJournalEntry(journalEntry);
        return updatedJournalLine;
    }

    @BeforeEach
    void initTest() {
        journalLine = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedJournalLine != null) {
            journalLineRepository.delete(insertedJournalLine);
            insertedJournalLine = null;
        }
    }

    @Test
    @Transactional
    void createJournalLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);
        var returnedJournalLineDTO = om.readValue(
            restJournalLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            JournalLineDTO.class
        );

        // Validate the JournalLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedJournalLine = journalLineMapper.toEntity(returnedJournalLineDTO);
        assertJournalLineUpdatableFieldsEquals(returnedJournalLine, getPersistedJournalLine(returnedJournalLine));

        insertedJournalLine = returnedJournalLine;
    }

    @Test
    @Transactional
    void createJournalLineWithExistingId() throws Exception {
        // Create the JournalLine with an existing ID
        journalLine.setId(1L);
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJournalLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJournalLines() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].debitAmount").value(hasItem(sameNumber(DEFAULT_DEBIT_AMOUNT))))
            .andExpect(jsonPath("$.[*].creditAmount").value(hasItem(sameNumber(DEFAULT_CREDIT_AMOUNT))));
    }

    @Test
    @Transactional
    void getJournalLine() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get the journalLine
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL_ID, journalLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journalLine.getId().intValue()))
            .andExpect(jsonPath("$.debitAmount").value(sameNumber(DEFAULT_DEBIT_AMOUNT)))
            .andExpect(jsonPath("$.creditAmount").value(sameNumber(DEFAULT_CREDIT_AMOUNT)));
    }

    @Test
    @Transactional
    void getJournalLinesByIdFiltering() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        Long id = journalLine.getId();

        defaultJournalLineFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultJournalLineFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultJournalLineFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount equals to
        defaultJournalLineFiltering("debitAmount.equals=" + DEFAULT_DEBIT_AMOUNT, "debitAmount.equals=" + UPDATED_DEBIT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount in
        defaultJournalLineFiltering(
            "debitAmount.in=" + DEFAULT_DEBIT_AMOUNT + "," + UPDATED_DEBIT_AMOUNT,
            "debitAmount.in=" + UPDATED_DEBIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount is not null
        defaultJournalLineFiltering("debitAmount.specified=true", "debitAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount is greater than or equal to
        defaultJournalLineFiltering(
            "debitAmount.greaterThanOrEqual=" + DEFAULT_DEBIT_AMOUNT,
            "debitAmount.greaterThanOrEqual=" + UPDATED_DEBIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount is less than or equal to
        defaultJournalLineFiltering(
            "debitAmount.lessThanOrEqual=" + DEFAULT_DEBIT_AMOUNT,
            "debitAmount.lessThanOrEqual=" + SMALLER_DEBIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount is less than
        defaultJournalLineFiltering("debitAmount.lessThan=" + UPDATED_DEBIT_AMOUNT, "debitAmount.lessThan=" + DEFAULT_DEBIT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllJournalLinesByDebitAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where debitAmount is greater than
        defaultJournalLineFiltering("debitAmount.greaterThan=" + SMALLER_DEBIT_AMOUNT, "debitAmount.greaterThan=" + DEFAULT_DEBIT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount equals to
        defaultJournalLineFiltering("creditAmount.equals=" + DEFAULT_CREDIT_AMOUNT, "creditAmount.equals=" + UPDATED_CREDIT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount in
        defaultJournalLineFiltering(
            "creditAmount.in=" + DEFAULT_CREDIT_AMOUNT + "," + UPDATED_CREDIT_AMOUNT,
            "creditAmount.in=" + UPDATED_CREDIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount is not null
        defaultJournalLineFiltering("creditAmount.specified=true", "creditAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount is greater than or equal to
        defaultJournalLineFiltering(
            "creditAmount.greaterThanOrEqual=" + DEFAULT_CREDIT_AMOUNT,
            "creditAmount.greaterThanOrEqual=" + UPDATED_CREDIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount is less than or equal to
        defaultJournalLineFiltering(
            "creditAmount.lessThanOrEqual=" + DEFAULT_CREDIT_AMOUNT,
            "creditAmount.lessThanOrEqual=" + SMALLER_CREDIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount is less than
        defaultJournalLineFiltering("creditAmount.lessThan=" + UPDATED_CREDIT_AMOUNT, "creditAmount.lessThan=" + DEFAULT_CREDIT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllJournalLinesByCreditAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        // Get all the journalLineList where creditAmount is greater than
        defaultJournalLineFiltering(
            "creditAmount.greaterThan=" + SMALLER_CREDIT_AMOUNT,
            "creditAmount.greaterThan=" + DEFAULT_CREDIT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllJournalLinesByAccountIsEqualToSomething() throws Exception {
        LedgerAccount account;
        if (TestUtil.findAll(em, LedgerAccount.class).isEmpty()) {
            journalLineRepository.saveAndFlush(journalLine);
            account = LedgerAccountResourceIT.createEntity();
        } else {
            account = TestUtil.findAll(em, LedgerAccount.class).getFirst();
        }
        em.persist(account);
        em.flush();
        journalLine.setAccount(account);
        journalLineRepository.saveAndFlush(journalLine);
        Long accountId = account.getId();
        // Get all the journalLineList where account equals to accountId
        defaultJournalLineShouldBeFound("accountId.equals=" + accountId);

        // Get all the journalLineList where account equals to (accountId + 1)
        defaultJournalLineShouldNotBeFound("accountId.equals=" + (accountId + 1));
    }

    @Test
    @Transactional
    void getAllJournalLinesByJournalEntryIsEqualToSomething() throws Exception {
        JournalEntry journalEntry;
        if (TestUtil.findAll(em, JournalEntry.class).isEmpty()) {
            journalLineRepository.saveAndFlush(journalLine);
            journalEntry = JournalEntryResourceIT.createEntity();
        } else {
            journalEntry = TestUtil.findAll(em, JournalEntry.class).getFirst();
        }
        em.persist(journalEntry);
        em.flush();
        journalLine.setJournalEntry(journalEntry);
        journalLineRepository.saveAndFlush(journalLine);
        Long journalEntryId = journalEntry.getId();
        // Get all the journalLineList where journalEntry equals to journalEntryId
        defaultJournalLineShouldBeFound("journalEntryId.equals=" + journalEntryId);

        // Get all the journalLineList where journalEntry equals to (journalEntryId + 1)
        defaultJournalLineShouldNotBeFound("journalEntryId.equals=" + (journalEntryId + 1));
    }

    private void defaultJournalLineFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultJournalLineShouldBeFound(shouldBeFound);
        defaultJournalLineShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJournalLineShouldBeFound(String filter) throws Exception {
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].debitAmount").value(hasItem(sameNumber(DEFAULT_DEBIT_AMOUNT))))
            .andExpect(jsonPath("$.[*].creditAmount").value(hasItem(sameNumber(DEFAULT_CREDIT_AMOUNT))));

        // Check, that the count call also returns 1
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJournalLineShouldNotBeFound(String filter) throws Exception {
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJournalLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingJournalLine() throws Exception {
        // Get the journalLine
        restJournalLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJournalLine() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalLine
        JournalLine updatedJournalLine = journalLineRepository.findById(journalLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJournalLine are not directly saved in db
        em.detach(updatedJournalLine);
        updatedJournalLine.debitAmount(UPDATED_DEBIT_AMOUNT).creditAmount(UPDATED_CREDIT_AMOUNT);
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(updatedJournalLine);

        restJournalLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJournalLineToMatchAllProperties(updatedJournalLine);
    }

    @Test
    @Transactional
    void putNonExistingJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJournalLineWithPatch() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalLine using partial update
        JournalLine partialUpdatedJournalLine = new JournalLine();
        partialUpdatedJournalLine.setId(journalLine.getId());

        partialUpdatedJournalLine.debitAmount(UPDATED_DEBIT_AMOUNT);

        restJournalLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalLine))
            )
            .andExpect(status().isOk());

        // Validate the JournalLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedJournalLine, journalLine),
            getPersistedJournalLine(journalLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateJournalLineWithPatch() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalLine using partial update
        JournalLine partialUpdatedJournalLine = new JournalLine();
        partialUpdatedJournalLine.setId(journalLine.getId());

        partialUpdatedJournalLine.debitAmount(UPDATED_DEBIT_AMOUNT).creditAmount(UPDATED_CREDIT_AMOUNT);

        restJournalLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalLine))
            )
            .andExpect(status().isOk());

        // Validate the JournalLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalLineUpdatableFieldsEquals(partialUpdatedJournalLine, getPersistedJournalLine(partialUpdatedJournalLine));
    }

    @Test
    @Transactional
    void patchNonExistingJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, journalLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJournalLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalLine.setId(longCount.incrementAndGet());

        // Create the JournalLine
        JournalLineDTO journalLineDTO = journalLineMapper.toDto(journalLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(journalLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJournalLine() throws Exception {
        // Initialize the database
        insertedJournalLine = journalLineRepository.saveAndFlush(journalLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the journalLine
        restJournalLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, journalLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return journalLineRepository.count();
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

    protected JournalLine getPersistedJournalLine(JournalLine journalLine) {
        return journalLineRepository.findById(journalLine.getId()).orElseThrow();
    }

    protected void assertPersistedJournalLineToMatchAllProperties(JournalLine expectedJournalLine) {
        assertJournalLineAllPropertiesEquals(expectedJournalLine, getPersistedJournalLine(expectedJournalLine));
    }

    protected void assertPersistedJournalLineToMatchUpdatableProperties(JournalLine expectedJournalLine) {
        assertJournalLineAllUpdatablePropertiesEquals(expectedJournalLine, getPersistedJournalLine(expectedJournalLine));
    }
}
