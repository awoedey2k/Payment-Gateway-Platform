package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.DisputeEvidenceRepository;
import io.paymentgateway.core.service.DisputeEvidenceQueryService;
import io.paymentgateway.core.service.DisputeEvidenceService;
import io.paymentgateway.core.service.criteria.DisputeEvidenceCriteria;
import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import io.paymentgateway.core.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.paymentgateway.core.domain.DisputeEvidence}.
 */
@RestController
@RequestMapping("/api/dispute-evidences")
public class DisputeEvidenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeEvidenceResource.class);

    private static final String ENTITY_NAME = "disputeEvidence";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final DisputeEvidenceService disputeEvidenceService;

    private final DisputeEvidenceRepository disputeEvidenceRepository;

    private final DisputeEvidenceQueryService disputeEvidenceQueryService;

    public DisputeEvidenceResource(
        DisputeEvidenceService disputeEvidenceService,
        DisputeEvidenceRepository disputeEvidenceRepository,
        DisputeEvidenceQueryService disputeEvidenceQueryService
    ) {
        this.disputeEvidenceService = disputeEvidenceService;
        this.disputeEvidenceRepository = disputeEvidenceRepository;
        this.disputeEvidenceQueryService = disputeEvidenceQueryService;
    }

    /**
     * {@code POST  /dispute-evidences} : Create a new disputeEvidence.
     *
     * @param disputeEvidenceDTO the disputeEvidenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new disputeEvidenceDTO, or with status {@code 400 (Bad Request)} if the disputeEvidence already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DisputeEvidenceDTO> createDisputeEvidence(@Valid @RequestBody DisputeEvidenceDTO disputeEvidenceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DisputeEvidence : {}", disputeEvidenceDTO);
        if (disputeEvidenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new disputeEvidence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        disputeEvidenceDTO = disputeEvidenceService.save(disputeEvidenceDTO);
        return ResponseEntity.created(new URI("/api/dispute-evidences/" + disputeEvidenceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, disputeEvidenceDTO.getId().toString()))
            .body(disputeEvidenceDTO);
    }

    /**
     * {@code PUT  /dispute-evidences/:id} : Updates an existing disputeEvidence.
     *
     * @param id the id of the disputeEvidenceDTO to save.
     * @param disputeEvidenceDTO the disputeEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disputeEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the disputeEvidenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the disputeEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisputeEvidenceDTO> updateDisputeEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DisputeEvidenceDTO disputeEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DisputeEvidence : {}, {}", id, disputeEvidenceDTO);
        if (disputeEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disputeEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disputeEvidenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        disputeEvidenceDTO = disputeEvidenceService.update(disputeEvidenceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, disputeEvidenceDTO.getId().toString()))
            .body(disputeEvidenceDTO);
    }

    /**
     * {@code PATCH  /dispute-evidences/:id} : Partial updates given fields of an existing disputeEvidence, field will ignore if it is null
     *
     * @param id the id of the disputeEvidenceDTO to save.
     * @param disputeEvidenceDTO the disputeEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disputeEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the disputeEvidenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the disputeEvidenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the disputeEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DisputeEvidenceDTO> partialUpdateDisputeEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DisputeEvidenceDTO disputeEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DisputeEvidence : {}, {}", id, disputeEvidenceDTO);
        if (disputeEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disputeEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disputeEvidenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DisputeEvidenceDTO> result = disputeEvidenceService.partialUpdate(disputeEvidenceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, disputeEvidenceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /dispute-evidences} : get all the Dispute Evidences.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Dispute Evidences in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DisputeEvidenceDTO>> getAllDisputeEvidences(
        DisputeEvidenceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DisputeEvidences by criteria: {}", criteria);

        Page<DisputeEvidenceDTO> page = disputeEvidenceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dispute-evidences/count} : count all the disputeEvidences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDisputeEvidences(DisputeEvidenceCriteria criteria) {
        LOG.debug("REST request to count DisputeEvidences by criteria: {}", criteria);
        return ResponseEntity.ok().body(disputeEvidenceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dispute-evidences/:id} : get the "id" disputeEvidence.
     *
     * @param id the id of the disputeEvidenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the disputeEvidenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisputeEvidenceDTO> getDisputeEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DisputeEvidence : {}", id);
        Optional<DisputeEvidenceDTO> disputeEvidenceDTO = disputeEvidenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(disputeEvidenceDTO);
    }

    /**
     * {@code DELETE  /dispute-evidences/:id} : delete the "id" disputeEvidence.
     *
     * @param id the id of the disputeEvidenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisputeEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DisputeEvidence : {}", id);
        disputeEvidenceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
