package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.CorporateTenantRepository;
import io.paymentgateway.core.service.CorporateTenantQueryService;
import io.paymentgateway.core.service.CorporateTenantService;
import io.paymentgateway.core.service.criteria.CorporateTenantCriteria;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.CorporateTenant}.
 */
@RestController
@RequestMapping("/api/corporate-tenants")
public class CorporateTenantResource {

    private static final Logger LOG = LoggerFactory.getLogger(CorporateTenantResource.class);

    private static final String ENTITY_NAME = "corporateTenant";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final CorporateTenantService corporateTenantService;

    private final CorporateTenantRepository corporateTenantRepository;

    private final CorporateTenantQueryService corporateTenantQueryService;

    public CorporateTenantResource(
        CorporateTenantService corporateTenantService,
        CorporateTenantRepository corporateTenantRepository,
        CorporateTenantQueryService corporateTenantQueryService
    ) {
        this.corporateTenantService = corporateTenantService;
        this.corporateTenantRepository = corporateTenantRepository;
        this.corporateTenantQueryService = corporateTenantQueryService;
    }

    /**
     * {@code POST  /corporate-tenants} : Create a new corporateTenant.
     *
     * @param corporateTenantDTO the corporateTenantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new corporateTenantDTO, or with status {@code 400 (Bad Request)} if the corporateTenant already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CorporateTenantDTO> createCorporateTenant(@Valid @RequestBody CorporateTenantDTO corporateTenantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CorporateTenant : {}", corporateTenantDTO);
        if (corporateTenantDTO.getId() != null) {
            throw new BadRequestAlertException("A new corporateTenant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        corporateTenantDTO = corporateTenantService.save(corporateTenantDTO);
        return ResponseEntity.created(new URI("/api/corporate-tenants/" + corporateTenantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, corporateTenantDTO.getId().toString()))
            .body(corporateTenantDTO);
    }

    /**
     * {@code PUT  /corporate-tenants/:id} : Updates an existing corporateTenant.
     *
     * @param id the id of the corporateTenantDTO to save.
     * @param corporateTenantDTO the corporateTenantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated corporateTenantDTO,
     * or with status {@code 400 (Bad Request)} if the corporateTenantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the corporateTenantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CorporateTenantDTO> updateCorporateTenant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CorporateTenantDTO corporateTenantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CorporateTenant : {}, {}", id, corporateTenantDTO);
        if (corporateTenantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, corporateTenantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!corporateTenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        corporateTenantDTO = corporateTenantService.update(corporateTenantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, corporateTenantDTO.getId().toString()))
            .body(corporateTenantDTO);
    }

    /**
     * {@code PATCH  /corporate-tenants/:id} : Partial updates given fields of an existing corporateTenant, field will ignore if it is null
     *
     * @param id the id of the corporateTenantDTO to save.
     * @param corporateTenantDTO the corporateTenantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated corporateTenantDTO,
     * or with status {@code 400 (Bad Request)} if the corporateTenantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the corporateTenantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the corporateTenantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CorporateTenantDTO> partialUpdateCorporateTenant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CorporateTenantDTO corporateTenantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CorporateTenant : {}, {}", id, corporateTenantDTO);
        if (corporateTenantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, corporateTenantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!corporateTenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CorporateTenantDTO> result = corporateTenantService.partialUpdate(corporateTenantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, corporateTenantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /corporate-tenants} : get all the Corporate Tenants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Corporate Tenants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CorporateTenantDTO>> getAllCorporateTenants(
        CorporateTenantCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CorporateTenants by criteria: {}", criteria);

        Page<CorporateTenantDTO> page = corporateTenantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /corporate-tenants/count} : count all the corporateTenants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCorporateTenants(CorporateTenantCriteria criteria) {
        LOG.debug("REST request to count CorporateTenants by criteria: {}", criteria);
        return ResponseEntity.ok().body(corporateTenantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /corporate-tenants/:id} : get the "id" corporateTenant.
     *
     * @param id the id of the corporateTenantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the corporateTenantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CorporateTenantDTO> getCorporateTenant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CorporateTenant : {}", id);
        Optional<CorporateTenantDTO> corporateTenantDTO = corporateTenantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(corporateTenantDTO);
    }

    /**
     * {@code DELETE  /corporate-tenants/:id} : delete the "id" corporateTenant.
     *
     * @param id the id of the corporateTenantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorporateTenant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CorporateTenant : {}", id);
        corporateTenantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
