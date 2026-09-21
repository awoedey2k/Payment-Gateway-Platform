package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.JournalEntryAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.repository.JournalEntryRepository;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
import io.paymentgateway.core.service.mapper.JournalEntryMapper;
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
 * Integration tests for the {@link JournalEntryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JournalEntryResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_POSTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_POSTED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/journal-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JournalEntryMapper journalEntryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJournalEntryMockMvc;

    private JournalEntry journalEntry;

    private JournalEntry insertedJournalEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntry createEntity() {
        return new JournalEntry().reference(DEFAULT_REFERENCE).description(DEFAULT_DESCRIPTION).postedAt(DEFAULT_POSTED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntry createUpdatedEntity() {
        return new JournalEntry().reference(UPDATED_REFERENCE).description(UPDATED_DESCRIPTION).postedAt(UPDATED_POSTED_AT);
    }

    @BeforeEach
    void initTest() {
        journalEntry = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedJournalEntry != null) {
            journalEntryRepository.delete(insertedJournalEntry);
            insertedJournalEntry = null;
        }
    }

    @Test
    @Transactional
    void createJournalEntry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);
        var returnedJournalEntryDTO = om.readValue(
            restJournalEntryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            JournalEntryDTO.class
        );

        // Validate the JournalEntry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedJournalEntry = journalEntryMapper.toEntity(returnedJournalEntryDTO);
        assertJournalEntryUpdatableFieldsEquals(returnedJournalEntry, getPersistedJournalEntry(returnedJournalEntry));

        insertedJournalEntry = returnedJournalEntry;
    }

    @Test
    @Transactional
    void createJournalEntryWithExistingId() throws Exception {
        // Create the JournalEntry with an existing ID
        journalEntry.setId(1L);
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalEntry.setReference(null);

        // Create the JournalEntry, which fails.
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalEntry.setDescription(null);

        // Create the JournalEntry, which fails.
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalEntry.setPostedAt(null);

        // Create the JournalEntry, which fails.
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJournalEntries() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].postedAt").value(hasItem(DEFAULT_POSTED_AT.toString())));
    }

    @Test
    @Transactional
    void getJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get the journalEntry
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, journalEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journalEntry.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.postedAt").value(DEFAULT_POSTED_AT.toString()));
    }

    @Test
    @Transactional
    void getJournalEntriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        Long id = journalEntry.getId();

        defaultJournalEntryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultJournalEntryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultJournalEntryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where reference equals to
        defaultJournalEntryFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where reference in
        defaultJournalEntryFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where reference is not null
        defaultJournalEntryFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalEntriesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where reference contains
        defaultJournalEntryFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where reference does not contain
        defaultJournalEntryFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where description equals to
        defaultJournalEntryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where description in
        defaultJournalEntryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllJournalEntriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where description is not null
        defaultJournalEntryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalEntriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where description contains
        defaultJournalEntryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where description does not contain
        defaultJournalEntryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllJournalEntriesByPostedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where postedAt equals to
        defaultJournalEntryFiltering("postedAt.equals=" + DEFAULT_POSTED_AT, "postedAt.equals=" + UPDATED_POSTED_AT);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByPostedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where postedAt in
        defaultJournalEntryFiltering("postedAt.in=" + DEFAULT_POSTED_AT + "," + UPDATED_POSTED_AT, "postedAt.in=" + UPDATED_POSTED_AT);
    }

    @Test
    @Transactional
    void getAllJournalEntriesByPostedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList where postedAt is not null
        defaultJournalEntryFiltering("postedAt.specified=true", "postedAt.specified=false");
    }

    private void defaultJournalEntryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultJournalEntryShouldBeFound(shouldBeFound);
        defaultJournalEntryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJournalEntryShouldBeFound(String filter) throws Exception {
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].postedAt").value(hasItem(DEFAULT_POSTED_AT.toString())));

        // Check, that the count call also returns 1
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJournalEntryShouldNotBeFound(String filter) throws Exception {
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingJournalEntry() throws Exception {
        // Get the journalEntry
        restJournalEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry
        JournalEntry updatedJournalEntry = journalEntryRepository.findById(journalEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJournalEntry are not directly saved in db
        em.detach(updatedJournalEntry);
        updatedJournalEntry.reference(UPDATED_REFERENCE).description(UPDATED_DESCRIPTION).postedAt(UPDATED_POSTED_AT);
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(updatedJournalEntry);

        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJournalEntryToMatchAllProperties(updatedJournalEntry);
    }

    @Test
    @Transactional
    void putNonExistingJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJournalEntryWithPatch() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry using partial update
        JournalEntry partialUpdatedJournalEntry = new JournalEntry();
        partialUpdatedJournalEntry.setId(journalEntry.getId());

        partialUpdatedJournalEntry.reference(UPDATED_REFERENCE).description(UPDATED_DESCRIPTION).postedAt(UPDATED_POSTED_AT);

        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalEntry))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalEntryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedJournalEntry, journalEntry),
            getPersistedJournalEntry(journalEntry)
        );
    }

    @Test
    @Transactional
    void fullUpdateJournalEntryWithPatch() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry using partial update
        JournalEntry partialUpdatedJournalEntry = new JournalEntry();
        partialUpdatedJournalEntry.setId(journalEntry.getId());

        partialUpdatedJournalEntry.reference(UPDATED_REFERENCE).description(UPDATED_DESCRIPTION).postedAt(UPDATED_POSTED_AT);

        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalEntry))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalEntryUpdatableFieldsEquals(partialUpdatedJournalEntry, getPersistedJournalEntry(partialUpdatedJournalEntry));
    }

    @Test
    @Transactional
    void patchNonExistingJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, journalEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // Create the JournalEntry
        JournalEntryDTO journalEntryDTO = journalEntryMapper.toDto(journalEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(journalEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the journalEntry
        restJournalEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, journalEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return journalEntryRepository.count();
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

    protected JournalEntry getPersistedJournalEntry(JournalEntry journalEntry) {
        return journalEntryRepository.findById(journalEntry.getId()).orElseThrow();
    }

    protected void assertPersistedJournalEntryToMatchAllProperties(JournalEntry expectedJournalEntry) {
        assertJournalEntryAllPropertiesEquals(expectedJournalEntry, getPersistedJournalEntry(expectedJournalEntry));
    }

    protected void assertPersistedJournalEntryToMatchUpdatableProperties(JournalEntry expectedJournalEntry) {
        assertJournalEntryAllUpdatablePropertiesEquals(expectedJournalEntry, getPersistedJournalEntry(expectedJournalEntry));
    }
}
