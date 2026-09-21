package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.TenantDirectorRepository;
import io.paymentgateway.core.service.TenantDirectorQueryService;
import io.paymentgateway.core.service.TenantDirectorService;
import io.paymentgateway.core.service.criteria.TenantDirectorCriteria;
import io.paymentgateway.core.service.dto.TenantDirectorDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.TenantDirector}.
 */
@RestController
@RequestMapping("/api/tenant-directors")
public class TenantDirectorResource {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDirectorResource.class);

    private static final String ENTITY_NAME = "tenantDirector";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final TenantDirectorService tenantDirectorService;

    private final TenantDirectorRepository tenantDirectorRepository;

    private final TenantDirectorQueryService tenantDirectorQueryService;

    public TenantDirectorResource(
        TenantDirectorService tenantDirectorService,
        TenantDirectorRepository tenantDirectorRepository,
        TenantDirectorQueryService tenantDirectorQueryService
    ) {
        this.tenantDirectorService = tenantDirectorService;
        this.tenantDirectorRepository = tenantDirectorRepository;
        this.tenantDirectorQueryService = tenantDirectorQueryService;
    }

    /**
     * {@code POST  /tenant-directors} : Create a new tenantDirector.
     *
     * @param tenantDirectorDTO the tenantDirectorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenantDirectorDTO, or with status {@code 400 (Bad Request)} if the tenantDirector already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TenantDirectorDTO> createTenantDirector(@Valid @RequestBody TenantDirectorDTO tenantDirectorDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TenantDirector : {}", tenantDirectorDTO);
        if (tenantDirectorDTO.getId() != null) {
            throw new BadRequestAlertException("A new tenantDirector cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tenantDirectorDTO = tenantDirectorService.save(tenantDirectorDTO);
        return ResponseEntity.created(new URI("/api/tenant-directors/" + tenantDirectorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tenantDirectorDTO.getId().toString()))
            .body(tenantDirectorDTO);
    }

    /**
     * {@code PUT  /tenant-directors/:id} : Updates an existing tenantDirector.
     *
     * @param id the id of the tenantDirectorDTO to save.
     * @param tenantDirectorDTO the tenantDirectorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantDirectorDTO,
     * or with status {@code 400 (Bad Request)} if the tenantDirectorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantDirectorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantDirectorDTO> updateTenantDirector(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TenantDirectorDTO tenantDirectorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TenantDirector : {}, {}", id, tenantDirectorDTO);
        if (tenantDirectorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantDirectorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantDirectorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tenantDirectorDTO = tenantDirectorService.update(tenantDirectorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantDirectorDTO.getId().toString()))
            .body(tenantDirectorDTO);
    }

    /**
     * {@code PATCH  /tenant-directors/:id} : Partial updates given fields of an existing tenantDirector, field will ignore if it is null
     *
     * @param id the id of the tenantDirectorDTO to save.
     * @param tenantDirectorDTO the tenantDirectorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantDirectorDTO,
     * or with status {@code 400 (Bad Request)} if the tenantDirectorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tenantDirectorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenantDirectorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TenantDirectorDTO> partialUpdateTenantDirector(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TenantDirectorDTO tenantDirectorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TenantDirector : {}, {}", id, tenantDirectorDTO);
        if (tenantDirectorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantDirectorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantDirectorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TenantDirectorDTO> result = tenantDirectorService.partialUpdate(tenantDirectorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantDirectorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tenant-directors} : get all the Tenant Directors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tenant Directors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TenantDirectorDTO>> getAllTenantDirectors(
        TenantDirectorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TenantDirectors by criteria: {}", criteria);

        Page<TenantDirectorDTO> page = tenantDirectorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tenant-directors/count} : count all the tenantDirectors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTenantDirectors(TenantDirectorCriteria criteria) {
        LOG.debug("REST request to count TenantDirectors by criteria: {}", criteria);
        return ResponseEntity.ok().body(tenantDirectorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tenant-directors/:id} : get the "id" tenantDirector.
     *
     * @param id the id of the tenantDirectorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenantDirectorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantDirectorDTO> getTenantDirector(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TenantDirector : {}", id);
        Optional<TenantDirectorDTO> tenantDirectorDTO = tenantDirectorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tenantDirectorDTO);
    }

    /**
     * {@code DELETE  /tenant-directors/:id} : delete the "id" tenantDirector.
     *
     * @param id the id of the tenantDirectorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenantDirector(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TenantDirector : {}", id);
        tenantDirectorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
