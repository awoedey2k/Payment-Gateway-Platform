package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.PaymentMethodAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.PaymentMethod;
import io.paymentgateway.core.domain.enumeration.PaymentMethodCategory;
import io.paymentgateway.core.repository.PaymentMethodRepository;
import io.paymentgateway.core.service.dto.PaymentMethodDTO;
import io.paymentgateway.core.service.mapper.PaymentMethodMapper;
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
 * Integration tests for the {@link PaymentMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentMethodResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final PaymentMethodCategory DEFAULT_CATEGORY = PaymentMethodCategory.CARD;
    private static final PaymentMethodCategory UPDATED_CATEGORY = PaymentMethodCategory.BANK_TRANSFER;

    private static final String ENTITY_API_URL = "/api/payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMethodMockMvc;

    private PaymentMethod paymentMethod;

    private PaymentMethod insertedPaymentMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createEntity() {
        return new PaymentMethod().code(DEFAULT_CODE).displayName(DEFAULT_DISPLAY_NAME).category(DEFAULT_CATEGORY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createUpdatedEntity() {
        return new PaymentMethod().code(UPDATED_CODE).displayName(UPDATED_DISPLAY_NAME).category(UPDATED_CATEGORY);
    }

    @BeforeEach
    void initTest() {
        paymentMethod = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentMethod != null) {
            paymentMethodRepository.delete(insertedPaymentMethod);
            insertedPaymentMethod = null;
        }
    }

    @Test
    @Transactional
    void createPaymentMethod() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);
        var returnedPaymentMethodDTO = om.readValue(
            restPaymentMethodMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentMethodDTO.class
        );

        // Validate the PaymentMethod in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentMethod = paymentMethodMapper.toEntity(returnedPaymentMethodDTO);
        assertPaymentMethodUpdatableFieldsEquals(returnedPaymentMethod, getPersistedPaymentMethod(returnedPaymentMethod));

        insertedPaymentMethod = returnedPaymentMethod;
    }

    @Test
    @Transactional
    void createPaymentMethodWithExistingId() throws Exception {
        // Create the PaymentMethod with an existing ID
        paymentMethod.setId(1L);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setCode(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setDisplayName(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setCategory(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentMethods() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));
    }

    @Test
    @Transactional
    void getPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get the paymentMethod
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentMethod.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()));
    }

    @Test
    @Transactional
    void getPaymentMethodsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        Long id = paymentMethod.getId();

        defaultPaymentMethodFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaymentMethodFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaymentMethodFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where code equals to
        defaultPaymentMethodFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where code in
        defaultPaymentMethodFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where code is not null
        defaultPaymentMethodFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where code contains
        defaultPaymentMethodFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where code does not contain
        defaultPaymentMethodFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByDisplayNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where displayName equals to
        defaultPaymentMethodFiltering("displayName.equals=" + DEFAULT_DISPLAY_NAME, "displayName.equals=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByDisplayNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where displayName in
        defaultPaymentMethodFiltering(
            "displayName.in=" + DEFAULT_DISPLAY_NAME + "," + UPDATED_DISPLAY_NAME,
            "displayName.in=" + UPDATED_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByDisplayNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where displayName is not null
        defaultPaymentMethodFiltering("displayName.specified=true", "displayName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByDisplayNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where displayName contains
        defaultPaymentMethodFiltering("displayName.contains=" + DEFAULT_DISPLAY_NAME, "displayName.contains=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByDisplayNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where displayName does not contain
        defaultPaymentMethodFiltering(
            "displayName.doesNotContain=" + UPDATED_DISPLAY_NAME,
            "displayName.doesNotContain=" + DEFAULT_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where category equals to
        defaultPaymentMethodFiltering("category.equals=" + DEFAULT_CATEGORY, "category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where category in
        defaultPaymentMethodFiltering("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY, "category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where category is not null
        defaultPaymentMethodFiltering("category.specified=true", "category.specified=false");
    }

    private void defaultPaymentMethodFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaymentMethodShouldBeFound(shouldBeFound);
        defaultPaymentMethodShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentMethodShouldBeFound(String filter) throws Exception {
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));

        // Check, that the count call also returns 1
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentMethodShouldNotBeFound(String filter) throws Exception {
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaymentMethod() throws Exception {
        // Get the paymentMethod
        restPaymentMethodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentMethod are not directly saved in db
        em.detach(updatedPaymentMethod);
        updatedPaymentMethod.code(UPDATED_CODE).displayName(UPDATED_DISPLAY_NAME).category(UPDATED_CATEGORY);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(updatedPaymentMethod);

        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentMethodToMatchAllProperties(updatedPaymentMethod);
    }

    @Test
    @Transactional
    void putNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod.displayName(UPDATED_DISPLAY_NAME).category(UPDATED_CATEGORY);

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentMethod, paymentMethod),
            getPersistedPaymentMethod(paymentMethod)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod.code(UPDATED_CODE).displayName(UPDATED_DISPLAY_NAME).category(UPDATED_CATEGORY);

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(partialUpdatedPaymentMethod, getPersistedPaymentMethod(partialUpdatedPaymentMethod));
    }

    @Test
    @Transactional
    void patchNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentMethod
        restPaymentMethodMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentMethod.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentMethodRepository.count();
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

    protected PaymentMethod getPersistedPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentMethodToMatchAllProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllPropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }

    protected void assertPersistedPaymentMethodToMatchUpdatableProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllUpdatablePropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }
}
