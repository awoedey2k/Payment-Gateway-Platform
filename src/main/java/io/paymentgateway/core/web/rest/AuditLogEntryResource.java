package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.AuditLogEntryRepository;
import io.paymentgateway.core.service.AuditLogEntryQueryService;
import io.paymentgateway.core.service.AuditLogEntryService;
import io.paymentgateway.core.service.criteria.AuditLogEntryCriteria;
import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.AuditLogEntry}.
 */
@RestController
@RequestMapping("/api/audit-log-entries")
public class AuditLogEntryResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogEntryResource.class);

    private static final String ENTITY_NAME = "auditLogEntry";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final AuditLogEntryService auditLogEntryService;

    private final AuditLogEntryRepository auditLogEntryRepository;

    private final AuditLogEntryQueryService auditLogEntryQueryService;

    public AuditLogEntryResource(
        AuditLogEntryService auditLogEntryService,
        AuditLogEntryRepository auditLogEntryRepository,
        AuditLogEntryQueryService auditLogEntryQueryService
    ) {
        this.auditLogEntryService = auditLogEntryService;
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.auditLogEntryQueryService = auditLogEntryQueryService;
    }

    /**
     * {@code POST  /audit-log-entries} : Create a new auditLogEntry.
     *
     * @param auditLogEntryDTO the auditLogEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditLogEntryDTO, or with status {@code 400 (Bad Request)} if the auditLogEntry already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AuditLogEntryDTO> createAuditLogEntry(@Valid @RequestBody AuditLogEntryDTO auditLogEntryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditLogEntry : {}", auditLogEntryDTO);
        if (auditLogEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditLogEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        auditLogEntryDTO = auditLogEntryService.save(auditLogEntryDTO);
        return ResponseEntity.created(new URI("/api/audit-log-entries/" + auditLogEntryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, auditLogEntryDTO.getId().toString()))
            .body(auditLogEntryDTO);
    }

    /**
     * {@code PUT  /audit-log-entries/:id} : Updates an existing auditLogEntry.
     *
     * @param id the id of the auditLogEntryDTO to save.
     * @param auditLogEntryDTO the auditLogEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditLogEntryDTO,
     * or with status {@code 400 (Bad Request)} if the auditLogEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditLogEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuditLogEntryDTO> updateAuditLogEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditLogEntryDTO auditLogEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditLogEntry : {}, {}", id, auditLogEntryDTO);
        if (auditLogEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditLogEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditLogEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        auditLogEntryDTO = auditLogEntryService.update(auditLogEntryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditLogEntryDTO.getId().toString()))
            .body(auditLogEntryDTO);
    }

    /**
     * {@code PATCH  /audit-log-entries/:id} : Partial updates given fields of an existing auditLogEntry, field will ignore if it is null
     *
     * @param id the id of the auditLogEntryDTO to save.
     * @param auditLogEntryDTO the auditLogEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditLogEntryDTO,
     * or with status {@code 400 (Bad Request)} if the auditLogEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditLogEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditLogEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuditLogEntryDTO> partialUpdateAuditLogEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditLogEntryDTO auditLogEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditLogEntry : {}, {}", id, auditLogEntryDTO);
        if (auditLogEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditLogEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditLogEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditLogEntryDTO> result = auditLogEntryService.partialUpdate(auditLogEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditLogEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /audit-log-entries} : get all the Audit Log Entries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Log Entries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuditLogEntryDTO>> getAllAuditLogEntries(
        AuditLogEntryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AuditLogEntries by criteria: {}", criteria);

        Page<AuditLogEntryDTO> page = auditLogEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /audit-log-entries/count} : count all the auditLogEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAuditLogEntries(AuditLogEntryCriteria criteria) {
        LOG.debug("REST request to count AuditLogEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(auditLogEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /audit-log-entries/:id} : get the "id" auditLogEntry.
     *
     * @param id the id of the auditLogEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditLogEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogEntryDTO> getAuditLogEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditLogEntry : {}", id);
        Optional<AuditLogEntryDTO> auditLogEntryDTO = auditLogEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditLogEntryDTO);
    }

    /**
     * {@code DELETE  /audit-log-entries/:id} : delete the "id" auditLogEntry.
     *
     * @param id the id of the auditLogEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLogEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditLogEntry : {}", id);
        auditLogEntryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
