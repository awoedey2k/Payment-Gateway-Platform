package io.paymentgateway.core.web.rest;

import static io.paymentgateway.core.domain.DisputeEvidenceAsserts.*;
import static io.paymentgateway.core.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.domain.DisputeEvidence;
import io.paymentgateway.core.domain.enumeration.DisputeEvidenceType;
import io.paymentgateway.core.repository.DisputeEvidenceRepository;
import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import io.paymentgateway.core.service.mapper.DisputeEvidenceMapper;
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
 * Integration tests for the {@link DisputeEvidenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisputeEvidenceResourceIT {

    private static final DisputeEvidenceType DEFAULT_EVIDENCE_TYPE = DisputeEvidenceType.PROOF_OF_DELIVERY;
    private static final DisputeEvidenceType UPDATED_EVIDENCE_TYPE = DisputeEvidenceType.CUSTOMER_COMMUNICATION;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE_BYTES = 1L;
    private static final Long UPDATED_FILE_SIZE_BYTES = 2L;
    private static final Long SMALLER_FILE_SIZE_BYTES = 1L - 1L;

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_256_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256_CHECKSUM = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPLOADED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED_AT = Instant.ofEpochMilli(1702048402568L);

    private static final String ENTITY_API_URL = "/api/dispute-evidences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisputeEvidenceRepository disputeEvidenceRepository;

    @Autowired
    private DisputeEvidenceMapper disputeEvidenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisputeEvidenceMockMvc;

    private DisputeEvidence disputeEvidence;

    private DisputeEvidence insertedDisputeEvidence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DisputeEvidence createEntity(EntityManager em) {
        DisputeEvidence disputeEvidence = new DisputeEvidence()
            .evidenceType(DEFAULT_EVIDENCE_TYPE)
            .fileName(DEFAULT_FILE_NAME)
            .fileUrl(DEFAULT_FILE_URL)
            .fileSizeBytes(DEFAULT_FILE_SIZE_BYTES)
            .mimeType(DEFAULT_MIME_TYPE)
            .sha256Checksum(DEFAULT_SHA_256_CHECKSUM)
            .uploadedAt(DEFAULT_UPLOADED_AT);
        // Add required entity
        Dispute dispute;
        if (TestUtil.findAll(em, Dispute.class).isEmpty()) {
            dispute = DisputeResourceIT.createEntity(em);
            em.persist(dispute);
            em.flush();
        } else {
            dispute = TestUtil.findAll(em, Dispute.class).getFirst();
        }
        disputeEvidence.setDispute(dispute);
        return disputeEvidence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DisputeEvidence createUpdatedEntity(EntityManager em) {
        DisputeEvidence updatedDisputeEvidence = new DisputeEvidence()
            .evidenceType(UPDATED_EVIDENCE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256Checksum(UPDATED_SHA_256_CHECKSUM)
            .uploadedAt(UPDATED_UPLOADED_AT);
        // Add required entity
        Dispute dispute;
        if (TestUtil.findAll(em, Dispute.class).isEmpty()) {
            dispute = DisputeResourceIT.createUpdatedEntity(em);
            em.persist(dispute);
            em.flush();
        } else {
            dispute = TestUtil.findAll(em, Dispute.class).getFirst();
        }
        updatedDisputeEvidence.setDispute(dispute);
        return updatedDisputeEvidence;
    }

    @BeforeEach
    void initTest() {
        disputeEvidence = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDisputeEvidence != null) {
            disputeEvidenceRepository.delete(insertedDisputeEvidence);
            insertedDisputeEvidence = null;
        }
    }

    @Test
    @Transactional
    void createDisputeEvidence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);
        var returnedDisputeEvidenceDTO = om.readValue(
            restDisputeEvidenceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DisputeEvidenceDTO.class
        );

        // Validate the DisputeEvidence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDisputeEvidence = disputeEvidenceMapper.toEntity(returnedDisputeEvidenceDTO);
        assertDisputeEvidenceUpdatableFieldsEquals(returnedDisputeEvidence, getPersistedDisputeEvidence(returnedDisputeEvidence));

        insertedDisputeEvidence = returnedDisputeEvidence;
    }

    @Test
    @Transactional
    void createDisputeEvidenceWithExistingId() throws Exception {
        // Create the DisputeEvidence with an existing ID
        disputeEvidence.setId(1L);
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEvidenceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setEvidenceType(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setFileName(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setFileUrl(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileSizeBytesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setFileSizeBytes(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMimeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setMimeType(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSha256ChecksumIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setSha256Checksum(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUploadedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        disputeEvidence.setUploadedAt(null);

        // Create the DisputeEvidence, which fails.
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDisputeEvidences() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disputeEvidence.getId().intValue())))
            .andExpect(jsonPath("$.[*].evidenceType").value(hasItem(DEFAULT_EVIDENCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].sha256Checksum").value(hasItem(DEFAULT_SHA_256_CHECKSUM)))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())));
    }

    @Test
    @Transactional
    void getDisputeEvidence() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get the disputeEvidence
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL_ID, disputeEvidence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(disputeEvidence.getId().intValue()))
            .andExpect(jsonPath("$.evidenceType").value(DEFAULT_EVIDENCE_TYPE.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.fileUrl").value(DEFAULT_FILE_URL))
            .andExpect(jsonPath("$.fileSizeBytes").value(DEFAULT_FILE_SIZE_BYTES.intValue()))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.sha256Checksum").value(DEFAULT_SHA_256_CHECKSUM))
            .andExpect(jsonPath("$.uploadedAt").value(DEFAULT_UPLOADED_AT.toString()));
    }

    @Test
    @Transactional
    void getDisputeEvidencesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        Long id = disputeEvidence.getId();

        defaultDisputeEvidenceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDisputeEvidenceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDisputeEvidenceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByEvidenceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where evidenceType equals to
        defaultDisputeEvidenceFiltering("evidenceType.equals=" + DEFAULT_EVIDENCE_TYPE, "evidenceType.equals=" + UPDATED_EVIDENCE_TYPE);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByEvidenceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where evidenceType in
        defaultDisputeEvidenceFiltering(
            "evidenceType.in=" + DEFAULT_EVIDENCE_TYPE + "," + UPDATED_EVIDENCE_TYPE,
            "evidenceType.in=" + UPDATED_EVIDENCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByEvidenceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where evidenceType is not null
        defaultDisputeEvidenceFiltering("evidenceType.specified=true", "evidenceType.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileName equals to
        defaultDisputeEvidenceFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileName in
        defaultDisputeEvidenceFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileName is not null
        defaultDisputeEvidenceFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileName contains
        defaultDisputeEvidenceFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileName does not contain
        defaultDisputeEvidenceFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileUrl equals to
        defaultDisputeEvidenceFiltering("fileUrl.equals=" + DEFAULT_FILE_URL, "fileUrl.equals=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileUrl in
        defaultDisputeEvidenceFiltering("fileUrl.in=" + DEFAULT_FILE_URL + "," + UPDATED_FILE_URL, "fileUrl.in=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileUrl is not null
        defaultDisputeEvidenceFiltering("fileUrl.specified=true", "fileUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileUrl contains
        defaultDisputeEvidenceFiltering("fileUrl.contains=" + DEFAULT_FILE_URL, "fileUrl.contains=" + UPDATED_FILE_URL);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileUrl does not contain
        defaultDisputeEvidenceFiltering("fileUrl.doesNotContain=" + UPDATED_FILE_URL, "fileUrl.doesNotContain=" + DEFAULT_FILE_URL);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes equals to
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.equals=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.equals=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes in
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.in=" + DEFAULT_FILE_SIZE_BYTES + "," + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.in=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes is not null
        defaultDisputeEvidenceFiltering("fileSizeBytes.specified=true", "fileSizeBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes is greater than or equal to
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.greaterThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThanOrEqual=" + UPDATED_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes is less than or equal to
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.lessThanOrEqual=" + DEFAULT_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThanOrEqual=" + SMALLER_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes is less than
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.lessThan=" + UPDATED_FILE_SIZE_BYTES,
            "fileSizeBytes.lessThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByFileSizeBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where fileSizeBytes is greater than
        defaultDisputeEvidenceFiltering(
            "fileSizeBytes.greaterThan=" + SMALLER_FILE_SIZE_BYTES,
            "fileSizeBytes.greaterThan=" + DEFAULT_FILE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByMimeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where mimeType equals to
        defaultDisputeEvidenceFiltering("mimeType.equals=" + DEFAULT_MIME_TYPE, "mimeType.equals=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByMimeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where mimeType in
        defaultDisputeEvidenceFiltering("mimeType.in=" + DEFAULT_MIME_TYPE + "," + UPDATED_MIME_TYPE, "mimeType.in=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByMimeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where mimeType is not null
        defaultDisputeEvidenceFiltering("mimeType.specified=true", "mimeType.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByMimeTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where mimeType contains
        defaultDisputeEvidenceFiltering("mimeType.contains=" + DEFAULT_MIME_TYPE, "mimeType.contains=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByMimeTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where mimeType does not contain
        defaultDisputeEvidenceFiltering("mimeType.doesNotContain=" + UPDATED_MIME_TYPE, "mimeType.doesNotContain=" + DEFAULT_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesBySha256ChecksumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where sha256Checksum equals to
        defaultDisputeEvidenceFiltering(
            "sha256Checksum.equals=" + DEFAULT_SHA_256_CHECKSUM,
            "sha256Checksum.equals=" + UPDATED_SHA_256_CHECKSUM
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesBySha256ChecksumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where sha256Checksum in
        defaultDisputeEvidenceFiltering(
            "sha256Checksum.in=" + DEFAULT_SHA_256_CHECKSUM + "," + UPDATED_SHA_256_CHECKSUM,
            "sha256Checksum.in=" + UPDATED_SHA_256_CHECKSUM
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesBySha256ChecksumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where sha256Checksum is not null
        defaultDisputeEvidenceFiltering("sha256Checksum.specified=true", "sha256Checksum.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesBySha256ChecksumContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where sha256Checksum contains
        defaultDisputeEvidenceFiltering(
            "sha256Checksum.contains=" + DEFAULT_SHA_256_CHECKSUM,
            "sha256Checksum.contains=" + UPDATED_SHA_256_CHECKSUM
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesBySha256ChecksumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where sha256Checksum does not contain
        defaultDisputeEvidenceFiltering(
            "sha256Checksum.doesNotContain=" + UPDATED_SHA_256_CHECKSUM,
            "sha256Checksum.doesNotContain=" + DEFAULT_SHA_256_CHECKSUM
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByUploadedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where uploadedAt equals to
        defaultDisputeEvidenceFiltering("uploadedAt.equals=" + DEFAULT_UPLOADED_AT, "uploadedAt.equals=" + UPDATED_UPLOADED_AT);
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByUploadedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where uploadedAt in
        defaultDisputeEvidenceFiltering(
            "uploadedAt.in=" + DEFAULT_UPLOADED_AT + "," + UPDATED_UPLOADED_AT,
            "uploadedAt.in=" + UPDATED_UPLOADED_AT
        );
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByUploadedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        // Get all the disputeEvidenceList where uploadedAt is not null
        defaultDisputeEvidenceFiltering("uploadedAt.specified=true", "uploadedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDisputeEvidencesByDisputeIsEqualToSomething() throws Exception {
        Dispute dispute;
        if (TestUtil.findAll(em, Dispute.class).isEmpty()) {
            disputeEvidenceRepository.saveAndFlush(disputeEvidence);
            dispute = DisputeResourceIT.createEntity(em);
        } else {
            dispute = TestUtil.findAll(em, Dispute.class).getFirst();
        }
        em.persist(dispute);
        em.flush();
        disputeEvidence.setDispute(dispute);
        disputeEvidenceRepository.saveAndFlush(disputeEvidence);
        Long disputeId = dispute.getId();
        // Get all the disputeEvidenceList where dispute equals to disputeId
        defaultDisputeEvidenceShouldBeFound("disputeId.equals=" + disputeId);

        // Get all the disputeEvidenceList where dispute equals to (disputeId + 1)
        defaultDisputeEvidenceShouldNotBeFound("disputeId.equals=" + (disputeId + 1));
    }

    private void defaultDisputeEvidenceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDisputeEvidenceShouldBeFound(shouldBeFound);
        defaultDisputeEvidenceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDisputeEvidenceShouldBeFound(String filter) throws Exception {
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disputeEvidence.getId().intValue())))
            .andExpect(jsonPath("$.[*].evidenceType").value(hasItem(DEFAULT_EVIDENCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].fileSizeBytes").value(hasItem(DEFAULT_FILE_SIZE_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].sha256Checksum").value(hasItem(DEFAULT_SHA_256_CHECKSUM)))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())));

        // Check, that the count call also returns 1
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDisputeEvidenceShouldNotBeFound(String filter) throws Exception {
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDisputeEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDisputeEvidence() throws Exception {
        // Get the disputeEvidence
        restDisputeEvidenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDisputeEvidence() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disputeEvidence
        DisputeEvidence updatedDisputeEvidence = disputeEvidenceRepository.findById(disputeEvidence.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDisputeEvidence are not directly saved in db
        em.detach(updatedDisputeEvidence);
        updatedDisputeEvidence
            .evidenceType(UPDATED_EVIDENCE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256Checksum(UPDATED_SHA_256_CHECKSUM)
            .uploadedAt(UPDATED_UPLOADED_AT);
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(updatedDisputeEvidence);

        restDisputeEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disputeEvidenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disputeEvidenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisputeEvidenceToMatchAllProperties(updatedDisputeEvidence);
    }

    @Test
    @Transactional
    void putNonExistingDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disputeEvidenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disputeEvidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disputeEvidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisputeEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disputeEvidence using partial update
        DisputeEvidence partialUpdatedDisputeEvidence = new DisputeEvidence();
        partialUpdatedDisputeEvidence.setId(disputeEvidence.getId());

        partialUpdatedDisputeEvidence
            .evidenceType(UPDATED_EVIDENCE_TYPE)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .mimeType(UPDATED_MIME_TYPE);

        restDisputeEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisputeEvidence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisputeEvidence))
            )
            .andExpect(status().isOk());

        // Validate the DisputeEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeEvidenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDisputeEvidence, disputeEvidence),
            getPersistedDisputeEvidence(disputeEvidence)
        );
    }

    @Test
    @Transactional
    void fullUpdateDisputeEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disputeEvidence using partial update
        DisputeEvidence partialUpdatedDisputeEvidence = new DisputeEvidence();
        partialUpdatedDisputeEvidence.setId(disputeEvidence.getId());

        partialUpdatedDisputeEvidence
            .evidenceType(UPDATED_EVIDENCE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .fileSizeBytes(UPDATED_FILE_SIZE_BYTES)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256Checksum(UPDATED_SHA_256_CHECKSUM)
            .uploadedAt(UPDATED_UPLOADED_AT);

        restDisputeEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisputeEvidence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisputeEvidence))
            )
            .andExpect(status().isOk());

        // Validate the DisputeEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeEvidenceUpdatableFieldsEquals(
            partialUpdatedDisputeEvidence,
            getPersistedDisputeEvidence(partialUpdatedDisputeEvidence)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, disputeEvidenceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disputeEvidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disputeEvidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDisputeEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disputeEvidence.setId(longCount.incrementAndGet());

        // Create the DisputeEvidence
        DisputeEvidenceDTO disputeEvidenceDTO = disputeEvidenceMapper.toDto(disputeEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisputeEvidenceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(disputeEvidenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DisputeEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDisputeEvidence() throws Exception {
        // Initialize the database
        insertedDisputeEvidence = disputeEvidenceRepository.saveAndFlush(disputeEvidence);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the disputeEvidence
        restDisputeEvidenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, disputeEvidence.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disputeEvidenceRepository.count();
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

    protected DisputeEvidence getPersistedDisputeEvidence(DisputeEvidence disputeEvidence) {
        return disputeEvidenceRepository.findById(disputeEvidence.getId()).orElseThrow();
    }

    protected void assertPersistedDisputeEvidenceToMatchAllProperties(DisputeEvidence expectedDisputeEvidence) {
        assertDisputeEvidenceAllPropertiesEquals(expectedDisputeEvidence, getPersistedDisputeEvidence(expectedDisputeEvidence));
    }

    protected void assertPersistedDisputeEvidenceToMatchUpdatableProperties(DisputeEvidence expectedDisputeEvidence) {
        assertDisputeEvidenceAllUpdatablePropertiesEquals(expectedDisputeEvidence, getPersistedDisputeEvidence(expectedDisputeEvidence));
    }
}
