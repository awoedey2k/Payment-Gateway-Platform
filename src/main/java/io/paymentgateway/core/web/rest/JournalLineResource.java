package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.JournalLineRepository;
import io.paymentgateway.core.service.JournalLineQueryService;
import io.paymentgateway.core.service.JournalLineService;
import io.paymentgateway.core.service.criteria.JournalLineCriteria;
import io.paymentgateway.core.service.dto.JournalLineDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.JournalLine}.
 */
@RestController
@RequestMapping("/api/journal-lines")
public class JournalLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(JournalLineResource.class);

    private static final String ENTITY_NAME = "journalLine";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final JournalLineService journalLineService;

    private final JournalLineRepository journalLineRepository;

    private final JournalLineQueryService journalLineQueryService;

    public JournalLineResource(
        JournalLineService journalLineService,
        JournalLineRepository journalLineRepository,
        JournalLineQueryService journalLineQueryService
    ) {
        this.journalLineService = journalLineService;
        this.journalLineRepository = journalLineRepository;
        this.journalLineQueryService = journalLineQueryService;
    }

    /**
     * {@code POST  /journal-lines} : Create a new journalLine.
     *
     * @param journalLineDTO the journalLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new journalLineDTO, or with status {@code 400 (Bad Request)} if the journalLine already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<JournalLineDTO> createJournalLine(@Valid @RequestBody JournalLineDTO journalLineDTO) throws URISyntaxException {
        LOG.debug("REST request to save JournalLine : {}", journalLineDTO);
        if (journalLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new journalLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        journalLineDTO = journalLineService.save(journalLineDTO);
        return ResponseEntity.created(new URI("/api/journal-lines/" + journalLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, journalLineDTO.getId().toString()))
            .body(journalLineDTO);
    }

    /**
     * {@code PUT  /journal-lines/:id} : Updates an existing journalLine.
     *
     * @param id the id of the journalLineDTO to save.
     * @param journalLineDTO the journalLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalLineDTO,
     * or with status {@code 400 (Bad Request)} if the journalLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the journalLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JournalLineDTO> updateJournalLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JournalLineDTO journalLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update JournalLine : {}, {}", id, journalLineDTO);
        if (journalLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        journalLineDTO = journalLineService.update(journalLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalLineDTO.getId().toString()))
            .body(journalLineDTO);
    }

    /**
     * {@code PATCH  /journal-lines/:id} : Partial updates given fields of an existing journalLine, field will ignore if it is null
     *
     * @param id the id of the journalLineDTO to save.
     * @param journalLineDTO the journalLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalLineDTO,
     * or with status {@code 400 (Bad Request)} if the journalLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the journalLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the journalLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JournalLineDTO> partialUpdateJournalLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JournalLineDTO journalLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update JournalLine : {}, {}", id, journalLineDTO);
        if (journalLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JournalLineDTO> result = journalLineService.partialUpdate(journalLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /journal-lines} : get all the Journal Lines.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Journal Lines in body.
     */
    @GetMapping("")
    public ResponseEntity<List<JournalLineDTO>> getAllJournalLines(
        JournalLineCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get JournalLines by criteria: {}", criteria);

        Page<JournalLineDTO> page = journalLineQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /journal-lines/count} : count all the journalLines.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countJournalLines(JournalLineCriteria criteria) {
        LOG.debug("REST request to count JournalLines by criteria: {}", criteria);
        return ResponseEntity.ok().body(journalLineQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /journal-lines/:id} : get the "id" journalLine.
     *
     * @param id the id of the journalLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the journalLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JournalLineDTO> getJournalLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get JournalLine : {}", id);
        Optional<JournalLineDTO> journalLineDTO = journalLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(journalLineDTO);
    }

    /**
     * {@code DELETE  /journal-lines/:id} : delete the "id" journalLine.
     *
     * @param id the id of the journalLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournalLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete JournalLine : {}", id);
        journalLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
