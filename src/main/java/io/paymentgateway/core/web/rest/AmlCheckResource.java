package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.AmlCheckRepository;
import io.paymentgateway.core.service.AmlCheckQueryService;
import io.paymentgateway.core.service.AmlCheckService;
import io.paymentgateway.core.service.criteria.AmlCheckCriteria;
import io.paymentgateway.core.service.dto.AmlCheckDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.AmlCheck}.
 */
@RestController
@RequestMapping("/api/aml-checks")
public class AmlCheckResource {

    private static final Logger LOG = LoggerFactory.getLogger(AmlCheckResource.class);

    private static final String ENTITY_NAME = "amlCheck";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final AmlCheckService amlCheckService;

    private final AmlCheckRepository amlCheckRepository;

    private final AmlCheckQueryService amlCheckQueryService;

    public AmlCheckResource(
        AmlCheckService amlCheckService,
        AmlCheckRepository amlCheckRepository,
        AmlCheckQueryService amlCheckQueryService
    ) {
        this.amlCheckService = amlCheckService;
        this.amlCheckRepository = amlCheckRepository;
        this.amlCheckQueryService = amlCheckQueryService;
    }

    /**
     * {@code POST  /aml-checks} : Create a new amlCheck.
     *
     * @param amlCheckDTO the amlCheckDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new amlCheckDTO, or with status {@code 400 (Bad Request)} if the amlCheck already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AmlCheckDTO> createAmlCheck(@Valid @RequestBody AmlCheckDTO amlCheckDTO) throws URISyntaxException {
        LOG.debug("REST request to save AmlCheck : {}", amlCheckDTO);
        if (amlCheckDTO.getId() != null) {
            throw new BadRequestAlertException("A new amlCheck cannot already have an ID", ENTITY_NAME, "idexists");
        }
        amlCheckDTO = amlCheckService.save(amlCheckDTO);
        return ResponseEntity.created(new URI("/api/aml-checks/" + amlCheckDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, amlCheckDTO.getId().toString()))
            .body(amlCheckDTO);
    }

    /**
     * {@code PUT  /aml-checks/:id} : Updates an existing amlCheck.
     *
     * @param id the id of the amlCheckDTO to save.
     * @param amlCheckDTO the amlCheckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amlCheckDTO,
     * or with status {@code 400 (Bad Request)} if the amlCheckDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the amlCheckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AmlCheckDTO> updateAmlCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AmlCheckDTO amlCheckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AmlCheck : {}, {}", id, amlCheckDTO);
        if (amlCheckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amlCheckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amlCheckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        amlCheckDTO = amlCheckService.update(amlCheckDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, amlCheckDTO.getId().toString()))
            .body(amlCheckDTO);
    }

    /**
     * {@code PATCH  /aml-checks/:id} : Partial updates given fields of an existing amlCheck, field will ignore if it is null
     *
     * @param id the id of the amlCheckDTO to save.
     * @param amlCheckDTO the amlCheckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amlCheckDTO,
     * or with status {@code 400 (Bad Request)} if the amlCheckDTO is not valid,
     * or with status {@code 404 (Not Found)} if the amlCheckDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the amlCheckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AmlCheckDTO> partialUpdateAmlCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AmlCheckDTO amlCheckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AmlCheck : {}, {}", id, amlCheckDTO);
        if (amlCheckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amlCheckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amlCheckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AmlCheckDTO> result = amlCheckService.partialUpdate(amlCheckDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, amlCheckDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /aml-checks} : get all the Aml Checks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Aml Checks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AmlCheckDTO>> getAllAmlChecks(
        AmlCheckCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AmlChecks by criteria: {}", criteria);

        Page<AmlCheckDTO> page = amlCheckQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aml-checks/count} : count all the amlChecks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAmlChecks(AmlCheckCriteria criteria) {
        LOG.debug("REST request to count AmlChecks by criteria: {}", criteria);
        return ResponseEntity.ok().body(amlCheckQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /aml-checks/:id} : get the "id" amlCheck.
     *
     * @param id the id of the amlCheckDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the amlCheckDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AmlCheckDTO> getAmlCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AmlCheck : {}", id);
        Optional<AmlCheckDTO> amlCheckDTO = amlCheckService.findOne(id);
        return ResponseUtil.wrapOrNotFound(amlCheckDTO);
    }

    /**
     * {@code DELETE  /aml-checks/:id} : delete the "id" amlCheck.
     *
     * @param id the id of the amlCheckDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmlCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AmlCheck : {}", id);
        amlCheckService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
