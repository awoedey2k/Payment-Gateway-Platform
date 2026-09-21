package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.PayoutScheduleAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static io.paymentgateway.core.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.PayoutSchedule;
import io.paymentgateway.core.domain.enumeration.PayoutFrequency;
import io.paymentgateway.core.repository.PayoutScheduleRepository;
import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
import io.paymentgateway.core.service.mapper.PayoutScheduleMapper;
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
 * Integration tests for the {@link PayoutScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PayoutScheduleResourceIT {

    private static final PayoutFrequency DEFAULT_FREQUENCY_MODE = PayoutFrequency.DAILY;
    private static final PayoutFrequency UPDATED_FREQUENCY_MODE = PayoutFrequency.WEEKLY;

    private static final BigDecimal DEFAULT_THRESHOLD_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_THRESHOLD_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_THRESHOLD_AMOUNT = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/payout-schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PayoutScheduleRepository payoutScheduleRepository;

    @Autowired
    private PayoutScheduleMapper payoutScheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPayoutScheduleMockMvc;

    private PayoutSchedule payoutSchedule;

    private PayoutSchedule insertedPayoutSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PayoutSchedule createEntity(EntityManager em) {
        PayoutSchedule payoutSchedule = new PayoutSchedule()
            .frequencyMode(DEFAULT_FREQUENCY_MODE)
            .thresholdAmount(DEFAULT_THRESHOLD_AMOUNT)
            .isActive(DEFAULT_IS_ACTIVE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        payoutSchedule.setTenant(corporateTenant);
        return payoutSchedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PayoutSchedule createUpdatedEntity(EntityManager em) {
        PayoutSchedule updatedPayoutSchedule = new PayoutSchedule()
            .frequencyMode(UPDATED_FREQUENCY_MODE)
            .thresholdAmount(UPDATED_THRESHOLD_AMOUNT)
            .isActive(UPDATED_IS_ACTIVE);
        // Add required entity
        CorporateTenant corporateTenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            corporateTenant = CorporateTenantResourceIT.createUpdatedEntity();
            em.persist(corporateTenant);
            em.flush();
        } else {
            corporateTenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        updatedPayoutSchedule.setTenant(corporateTenant);
        return updatedPayoutSchedule;
    }

    @BeforeEach
    void initTest() {
        payoutSchedule = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPayoutSchedule != null) {
            payoutScheduleRepository.delete(insertedPayoutSchedule);
            insertedPayoutSchedule = null;
        }
    }

    @Test
    @Transactional
    void createPayoutSchedule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);
        var returnedPayoutScheduleDTO = om.readValue(
            restPayoutScheduleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PayoutScheduleDTO.class
        );

        // Validate the PayoutSchedule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPayoutSchedule = payoutScheduleMapper.toEntity(returnedPayoutScheduleDTO);
        assertPayoutScheduleUpdatableFieldsEquals(returnedPayoutSchedule, getPersistedPayoutSchedule(returnedPayoutSchedule));

        insertedPayoutSchedule = returnedPayoutSchedule;
    }

    @Test
    @Transactional
    void createPayoutScheduleWithExistingId() throws Exception {
        // Create the PayoutSchedule with an existing ID
        payoutSchedule.setId(1L);
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPayoutScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFrequencyModeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutSchedule.setFrequencyMode(null);

        // Create the PayoutSchedule, which fails.
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        restPayoutScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThresholdAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutSchedule.setThresholdAmount(null);

        // Create the PayoutSchedule, which fails.
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        restPayoutScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payoutSchedule.setIsActive(null);

        // Create the PayoutSchedule, which fails.
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        restPayoutScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPayoutSchedules() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payoutSchedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].frequencyMode").value(hasItem(DEFAULT_FREQUENCY_MODE.toString())))
            .andExpect(jsonPath("$.[*].thresholdAmount").value(hasItem(sameNumber(DEFAULT_THRESHOLD_AMOUNT))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getPayoutSchedule() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get the payoutSchedule
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, payoutSchedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payoutSchedule.getId().intValue()))
            .andExpect(jsonPath("$.frequencyMode").value(DEFAULT_FREQUENCY_MODE.toString()))
            .andExpect(jsonPath("$.thresholdAmount").value(sameNumber(DEFAULT_THRESHOLD_AMOUNT)))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getPayoutSchedulesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        Long id = payoutSchedule.getId();

        defaultPayoutScheduleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPayoutScheduleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPayoutScheduleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByFrequencyModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where frequencyMode equals to
        defaultPayoutScheduleFiltering("frequencyMode.equals=" + DEFAULT_FREQUENCY_MODE, "frequencyMode.equals=" + UPDATED_FREQUENCY_MODE);
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByFrequencyModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where frequencyMode in
        defaultPayoutScheduleFiltering(
            "frequencyMode.in=" + DEFAULT_FREQUENCY_MODE + "," + UPDATED_FREQUENCY_MODE,
            "frequencyMode.in=" + UPDATED_FREQUENCY_MODE
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByFrequencyModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where frequencyMode is not null
        defaultPayoutScheduleFiltering("frequencyMode.specified=true", "frequencyMode.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount equals to
        defaultPayoutScheduleFiltering(
            "thresholdAmount.equals=" + DEFAULT_THRESHOLD_AMOUNT,
            "thresholdAmount.equals=" + UPDATED_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount in
        defaultPayoutScheduleFiltering(
            "thresholdAmount.in=" + DEFAULT_THRESHOLD_AMOUNT + "," + UPDATED_THRESHOLD_AMOUNT,
            "thresholdAmount.in=" + UPDATED_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount is not null
        defaultPayoutScheduleFiltering("thresholdAmount.specified=true", "thresholdAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount is greater than or equal to
        defaultPayoutScheduleFiltering(
            "thresholdAmount.greaterThanOrEqual=" + DEFAULT_THRESHOLD_AMOUNT,
            "thresholdAmount.greaterThanOrEqual=" + UPDATED_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount is less than or equal to
        defaultPayoutScheduleFiltering(
            "thresholdAmount.lessThanOrEqual=" + DEFAULT_THRESHOLD_AMOUNT,
            "thresholdAmount.lessThanOrEqual=" + SMALLER_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount is less than
        defaultPayoutScheduleFiltering(
            "thresholdAmount.lessThan=" + UPDATED_THRESHOLD_AMOUNT,
            "thresholdAmount.lessThan=" + DEFAULT_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByThresholdAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where thresholdAmount is greater than
        defaultPayoutScheduleFiltering(
            "thresholdAmount.greaterThan=" + SMALLER_THRESHOLD_AMOUNT,
            "thresholdAmount.greaterThan=" + DEFAULT_THRESHOLD_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where isActive equals to
        defaultPayoutScheduleFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where isActive in
        defaultPayoutScheduleFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        // Get all the payoutScheduleList where isActive is not null
        defaultPayoutScheduleFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllPayoutSchedulesByTenantIsEqualToSomething() throws Exception {
        CorporateTenant tenant;
        if (TestUtil.findAll(em, CorporateTenant.class).isEmpty()) {
            payoutScheduleRepository.saveAndFlush(payoutSchedule);
            tenant = CorporateTenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, CorporateTenant.class).getFirst();
        }
        em.persist(tenant);
        em.flush();
        payoutSchedule.setTenant(tenant);
        payoutScheduleRepository.saveAndFlush(payoutSchedule);
        Long tenantId = tenant.getId();
        // Get all the payoutScheduleList where tenant equals to tenantId
        defaultPayoutScheduleShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the payoutScheduleList where tenant equals to (tenantId + 1)
        defaultPayoutScheduleShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultPayoutScheduleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPayoutScheduleShouldBeFound(shouldBeFound);
        defaultPayoutScheduleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPayoutScheduleShouldBeFound(String filter) throws Exception {
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payoutSchedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].frequencyMode").value(hasItem(DEFAULT_FREQUENCY_MODE.toString())))
            .andExpect(jsonPath("$.[*].thresholdAmount").value(hasItem(sameNumber(DEFAULT_THRESHOLD_AMOUNT))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPayoutScheduleShouldNotBeFound(String filter) throws Exception {
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPayoutScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPayoutSchedule() throws Exception {
        // Get the payoutSchedule
        restPayoutScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayoutSchedule() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutSchedule
        PayoutSchedule updatedPayoutSchedule = payoutScheduleRepository.findById(payoutSchedule.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPayoutSchedule are not directly saved in db
        em.detach(updatedPayoutSchedule);
        updatedPayoutSchedule.frequencyMode(UPDATED_FREQUENCY_MODE).thresholdAmount(UPDATED_THRESHOLD_AMOUNT).isActive(UPDATED_IS_ACTIVE);
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(updatedPayoutSchedule);

        restPayoutScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, payoutScheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutScheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPayoutScheduleToMatchAllProperties(updatedPayoutSchedule);
    }

    @Test
    @Transactional
    void putNonExistingPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, payoutScheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(payoutScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePayoutScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutSchedule using partial update
        PayoutSchedule partialUpdatedPayoutSchedule = new PayoutSchedule();
        partialUpdatedPayoutSchedule.setId(payoutSchedule.getId());

        partialUpdatedPayoutSchedule.frequencyMode(UPDATED_FREQUENCY_MODE);

        restPayoutScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayoutSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayoutSchedule))
            )
            .andExpect(status().isOk());

        // Validate the PayoutSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPayoutScheduleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPayoutSchedule, payoutSchedule),
            getPersistedPayoutSchedule(payoutSchedule)
        );
    }

    @Test
    @Transactional
    void fullUpdatePayoutScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payoutSchedule using partial update
        PayoutSchedule partialUpdatedPayoutSchedule = new PayoutSchedule();
        partialUpdatedPayoutSchedule.setId(payoutSchedule.getId());

        partialUpdatedPayoutSchedule
            .frequencyMode(UPDATED_FREQUENCY_MODE)
            .thresholdAmount(UPDATED_THRESHOLD_AMOUNT)
            .isActive(UPDATED_IS_ACTIVE);

        restPayoutScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayoutSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayoutSchedule))
            )
            .andExpect(status().isOk());

        // Validate the PayoutSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPayoutScheduleUpdatableFieldsEquals(partialUpdatedPayoutSchedule, getPersistedPayoutSchedule(partialUpdatedPayoutSchedule));
    }

    @Test
    @Transactional
    void patchNonExistingPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, payoutScheduleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(payoutScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(payoutScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayoutSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payoutSchedule.setId(longCount.incrementAndGet());

        // Create the PayoutSchedule
        PayoutScheduleDTO payoutScheduleDTO = payoutScheduleMapper.toDto(payoutSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPayoutScheduleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(payoutScheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PayoutSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePayoutSchedule() throws Exception {
        // Initialize the database
        insertedPayoutSchedule = payoutScheduleRepository.saveAndFlush(payoutSchedule);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the payoutSchedule
        restPayoutScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, payoutSchedule.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return payoutScheduleRepository.count();
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

    protected PayoutSchedule getPersistedPayoutSchedule(PayoutSchedule payoutSchedule) {
        return payoutScheduleRepository.findById(payoutSchedule.getId()).orElseThrow();
    }

    protected void assertPersistedPayoutScheduleToMatchAllProperties(PayoutSchedule expectedPayoutSchedule) {
        assertPayoutScheduleAllPropertiesEquals(expectedPayoutSchedule, getPersistedPayoutSchedule(expectedPayoutSchedule));
    }

    protected void assertPersistedPayoutScheduleToMatchUpdatableProperties(PayoutSchedule expectedPayoutSchedule) {
        assertPayoutScheduleAllUpdatablePropertiesEquals(expectedPayoutSchedule, getPersistedPayoutSchedule(expectedPayoutSchedule));
    }
}
