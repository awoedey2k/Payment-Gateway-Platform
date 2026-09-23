package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.TenantDomainRepository;
import io.paymentgateway.core.service.TenantDomainQueryService;
import io.paymentgateway.core.service.TenantDomainService;
import io.paymentgateway.core.service.criteria.TenantDomainCriteria;
import io.paymentgateway.core.service.dto.TenantDomainDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.TenantDomain}.
 */
@RestController
@RequestMapping("/api/tenant-domains")
public class TenantDomainResource {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDomainResource.class);

    private static final String ENTITY_NAME = "tenantDomain";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final TenantDomainService tenantDomainService;

    private final TenantDomainRepository tenantDomainRepository;

    private final TenantDomainQueryService tenantDomainQueryService;

    public TenantDomainResource(
        TenantDomainService tenantDomainService,
        TenantDomainRepository tenantDomainRepository,
        TenantDomainQueryService tenantDomainQueryService
    ) {
        this.tenantDomainService = tenantDomainService;
        this.tenantDomainRepository = tenantDomainRepository;
        this.tenantDomainQueryService = tenantDomainQueryService;
    }

    /**
     * {@code POST  /tenant-domains} : Create a new tenantDomain.
     *
     * @param tenantDomainDTO the tenantDomainDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenantDomainDTO, or with status {@code 400 (Bad Request)} if the tenantDomain already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TenantDomainDTO> createTenantDomain(@Valid @RequestBody TenantDomainDTO tenantDomainDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TenantDomain : {}", tenantDomainDTO);
        if (tenantDomainDTO.getId() != null) {
            throw new BadRequestAlertException("A new tenantDomain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tenantDomainDTO = tenantDomainService.save(tenantDomainDTO);
        return ResponseEntity.created(new URI("/api/tenant-domains/" + tenantDomainDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tenantDomainDTO.getId().toString()))
            .body(tenantDomainDTO);
    }

    /**
     * {@code PUT  /tenant-domains/:id} : Updates an existing tenantDomain.
     *
     * @param id the id of the tenantDomainDTO to save.
     * @param tenantDomainDTO the tenantDomainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantDomainDTO,
     * or with status {@code 400 (Bad Request)} if the tenantDomainDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantDomainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantDomainDTO> updateTenantDomain(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TenantDomainDTO tenantDomainDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TenantDomain : {}, {}", id, tenantDomainDTO);
        if (tenantDomainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantDomainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantDomainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tenantDomainDTO = tenantDomainService.update(tenantDomainDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantDomainDTO.getId().toString()))
            .body(tenantDomainDTO);
    }

    /**
     * {@code PATCH  /tenant-domains/:id} : Partial updates given fields of an existing tenantDomain, field will ignore if it is null
     *
     * @param id the id of the tenantDomainDTO to save.
     * @param tenantDomainDTO the tenantDomainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantDomainDTO,
     * or with status {@code 400 (Bad Request)} if the tenantDomainDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tenantDomainDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenantDomainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TenantDomainDTO> partialUpdateTenantDomain(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TenantDomainDTO tenantDomainDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TenantDomain : {}, {}", id, tenantDomainDTO);
        if (tenantDomainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantDomainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantDomainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TenantDomainDTO> result = tenantDomainService.partialUpdate(tenantDomainDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantDomainDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tenant-domains} : get all the Tenant Domains.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tenant Domains in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TenantDomainDTO>> getAllTenantDomains(
        TenantDomainCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TenantDomains by criteria: {}", criteria);

        Page<TenantDomainDTO> page = tenantDomainQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tenant-domains/count} : count all the tenantDomains.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTenantDomains(TenantDomainCriteria criteria) {
        LOG.debug("REST request to count TenantDomains by criteria: {}", criteria);
        return ResponseEntity.ok().body(tenantDomainQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tenant-domains/:id} : get the "id" tenantDomain.
     *
     * @param id the id of the tenantDomainDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenantDomainDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantDomainDTO> getTenantDomain(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TenantDomain : {}", id);
        Optional<TenantDomainDTO> tenantDomainDTO = tenantDomainService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tenantDomainDTO);
    }

    /**
     * {@code DELETE  /tenant-domains/:id} : delete the "id" tenantDomain.
     *
     * @param id the id of the tenantDomainDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenantDomain(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TenantDomain : {}", id);
        tenantDomainService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
