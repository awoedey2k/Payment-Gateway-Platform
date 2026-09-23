package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.DisputeRepository;
import io.paymentgateway.core.service.DisputeQueryService;
import io.paymentgateway.core.service.DisputeService;
import io.paymentgateway.core.service.criteria.DisputeCriteria;
import io.paymentgateway.core.service.dto.DisputeDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.Dispute}.
 */
@RestController
@RequestMapping("/api/disputes")
public class DisputeResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeResource.class);

    private static final String ENTITY_NAME = "dispute";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final DisputeService disputeService;

    private final DisputeRepository disputeRepository;

    private final DisputeQueryService disputeQueryService;

    public DisputeResource(DisputeService disputeService, DisputeRepository disputeRepository, DisputeQueryService disputeQueryService) {
        this.disputeService = disputeService;
        this.disputeRepository = disputeRepository;
        this.disputeQueryService = disputeQueryService;
    }

    /**
     * {@code POST  /disputes} : Create a new dispute.
     *
     * @param disputeDTO the disputeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new disputeDTO, or with status {@code 400 (Bad Request)} if the dispute already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DisputeDTO> createDispute(@Valid @RequestBody DisputeDTO disputeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Dispute : {}", disputeDTO);
        if (disputeDTO.getId() != null) {
            throw new BadRequestAlertException("A new dispute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        disputeDTO = disputeService.save(disputeDTO);
        return ResponseEntity.created(new URI("/api/disputes/" + disputeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, disputeDTO.getId().toString()))
            .body(disputeDTO);
    }

    /**
     * {@code PUT  /disputes/:id} : Updates an existing dispute.
     *
     * @param id the id of the disputeDTO to save.
     * @param disputeDTO the disputeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disputeDTO,
     * or with status {@code 400 (Bad Request)} if the disputeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the disputeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisputeDTO> updateDispute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DisputeDTO disputeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Dispute : {}, {}", id, disputeDTO);
        if (disputeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disputeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disputeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        disputeDTO = disputeService.update(disputeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, disputeDTO.getId().toString()))
            .body(disputeDTO);
    }

    /**
     * {@code PATCH  /disputes/:id} : Partial updates given fields of an existing dispute, field will ignore if it is null
     *
     * @param id the id of the disputeDTO to save.
     * @param disputeDTO the disputeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disputeDTO,
     * or with status {@code 400 (Bad Request)} if the disputeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the disputeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the disputeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DisputeDTO> partialUpdateDispute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DisputeDTO disputeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Dispute : {}, {}", id, disputeDTO);
        if (disputeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disputeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disputeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DisputeDTO> result = disputeService.partialUpdate(disputeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, disputeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /disputes} : get all the Disputes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Disputes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DisputeDTO>> getAllDisputes(
        DisputeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Disputes by criteria: {}", criteria);

        Page<DisputeDTO> page = disputeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /disputes/count} : count all the disputes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDisputes(DisputeCriteria criteria) {
        LOG.debug("REST request to count Disputes by criteria: {}", criteria);
        return ResponseEntity.ok().body(disputeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /disputes/:id} : get the "id" dispute.
     *
     * @param id the id of the disputeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the disputeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisputeDTO> getDispute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Dispute : {}", id);
        Optional<DisputeDTO> disputeDTO = disputeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(disputeDTO);
    }

    /**
     * {@code DELETE  /disputes/:id} : delete the "id" dispute.
     *
     * @param id the id of the disputeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Dispute : {}", id);
        disputeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
