package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.JournalEntryRepository;
import io.paymentgateway.core.service.JournalEntryQueryService;
import io.paymentgateway.core.service.JournalEntryService;
import io.paymentgateway.core.service.criteria.JournalEntryCriteria;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.JournalEntry}.
 */
@RestController
@RequestMapping("/api/journal-entries")
public class JournalEntryResource {

    private static final Logger LOG = LoggerFactory.getLogger(JournalEntryResource.class);

    private static final String ENTITY_NAME = "journalEntry";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final JournalEntryService journalEntryService;

    private final JournalEntryRepository journalEntryRepository;

    private final JournalEntryQueryService journalEntryQueryService;

    public JournalEntryResource(
        JournalEntryService journalEntryService,
        JournalEntryRepository journalEntryRepository,
        JournalEntryQueryService journalEntryQueryService
    ) {
        this.journalEntryService = journalEntryService;
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryQueryService = journalEntryQueryService;
    }

    /**
     * {@code POST  /journal-entries} : Create a new journalEntry.
     *
     * @param journalEntryDTO the journalEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new journalEntryDTO, or with status {@code 400 (Bad Request)} if the journalEntry already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(@Valid @RequestBody JournalEntryDTO journalEntryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save JournalEntry : {}", journalEntryDTO);
        if (journalEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new journalEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        journalEntryDTO = journalEntryService.save(journalEntryDTO);
        return ResponseEntity.created(new URI("/api/journal-entries/" + journalEntryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, journalEntryDTO.getId().toString()))
            .body(journalEntryDTO);
    }

    /**
     * {@code PUT  /journal-entries/:id} : Updates an existing journalEntry.
     *
     * @param id the id of the journalEntryDTO to save.
     * @param journalEntryDTO the journalEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalEntryDTO,
     * or with status {@code 400 (Bad Request)} if the journalEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the journalEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> updateJournalEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JournalEntryDTO journalEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update JournalEntry : {}, {}", id, journalEntryDTO);
        if (journalEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        journalEntryDTO = journalEntryService.update(journalEntryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalEntryDTO.getId().toString()))
            .body(journalEntryDTO);
    }

    /**
     * {@code PATCH  /journal-entries/:id} : Partial updates given fields of an existing journalEntry, field will ignore if it is null
     *
     * @param id the id of the journalEntryDTO to save.
     * @param journalEntryDTO the journalEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalEntryDTO,
     * or with status {@code 400 (Bad Request)} if the journalEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the journalEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the journalEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JournalEntryDTO> partialUpdateJournalEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JournalEntryDTO journalEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update JournalEntry : {}, {}", id, journalEntryDTO);
        if (journalEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JournalEntryDTO> result = journalEntryService.partialUpdate(journalEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /journal-entries} : get all the Journal Entries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Journal Entries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<JournalEntryDTO>> getAllJournalEntries(
        JournalEntryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get JournalEntries by criteria: {}", criteria);

        Page<JournalEntryDTO> page = journalEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /journal-entries/count} : count all the journalEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countJournalEntries(JournalEntryCriteria criteria) {
        LOG.debug("REST request to count JournalEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(journalEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /journal-entries/:id} : get the "id" journalEntry.
     *
     * @param id the id of the journalEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the journalEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get JournalEntry : {}", id);
        Optional<JournalEntryDTO> journalEntryDTO = journalEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(journalEntryDTO);
    }

    /**
     * {@code DELETE  /journal-entries/:id} : delete the "id" journalEntry.
     *
     * @param id the id of the journalEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournalEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete JournalEntry : {}", id);
        journalEntryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
