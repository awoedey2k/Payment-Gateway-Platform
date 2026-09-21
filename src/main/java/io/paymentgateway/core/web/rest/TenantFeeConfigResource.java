package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.TenantFeeConfigRepository;
import io.paymentgateway.core.service.TenantFeeConfigQueryService;
import io.paymentgateway.core.service.TenantFeeConfigService;
import io.paymentgateway.core.service.criteria.TenantFeeConfigCriteria;
import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.TenantFeeConfig}.
 */
@RestController
@RequestMapping("/api/tenant-fee-configs")
public class TenantFeeConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(TenantFeeConfigResource.class);

    private static final String ENTITY_NAME = "tenantFeeConfig";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final TenantFeeConfigService tenantFeeConfigService;

    private final TenantFeeConfigRepository tenantFeeConfigRepository;

    private final TenantFeeConfigQueryService tenantFeeConfigQueryService;

    public TenantFeeConfigResource(
        TenantFeeConfigService tenantFeeConfigService,
        TenantFeeConfigRepository tenantFeeConfigRepository,
        TenantFeeConfigQueryService tenantFeeConfigQueryService
    ) {
        this.tenantFeeConfigService = tenantFeeConfigService;
        this.tenantFeeConfigRepository = tenantFeeConfigRepository;
        this.tenantFeeConfigQueryService = tenantFeeConfigQueryService;
    }

    /**
     * {@code POST  /tenant-fee-configs} : Create a new tenantFeeConfig.
     *
     * @param tenantFeeConfigDTO the tenantFeeConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenantFeeConfigDTO, or with status {@code 400 (Bad Request)} if the tenantFeeConfig already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TenantFeeConfigDTO> createTenantFeeConfig(@Valid @RequestBody TenantFeeConfigDTO tenantFeeConfigDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TenantFeeConfig : {}", tenantFeeConfigDTO);
        if (tenantFeeConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new tenantFeeConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tenantFeeConfigDTO = tenantFeeConfigService.save(tenantFeeConfigDTO);
        return ResponseEntity.created(new URI("/api/tenant-fee-configs/" + tenantFeeConfigDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tenantFeeConfigDTO.getId().toString()))
            .body(tenantFeeConfigDTO);
    }

    /**
     * {@code PUT  /tenant-fee-configs/:id} : Updates an existing tenantFeeConfig.
     *
     * @param id the id of the tenantFeeConfigDTO to save.
     * @param tenantFeeConfigDTO the tenantFeeConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantFeeConfigDTO,
     * or with status {@code 400 (Bad Request)} if the tenantFeeConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantFeeConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantFeeConfigDTO> updateTenantFeeConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TenantFeeConfigDTO tenantFeeConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TenantFeeConfig : {}, {}", id, tenantFeeConfigDTO);
        if (tenantFeeConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantFeeConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantFeeConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tenantFeeConfigDTO = tenantFeeConfigService.update(tenantFeeConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantFeeConfigDTO.getId().toString()))
            .body(tenantFeeConfigDTO);
    }

    /**
     * {@code PATCH  /tenant-fee-configs/:id} : Partial updates given fields of an existing tenantFeeConfig, field will ignore if it is null
     *
     * @param id the id of the tenantFeeConfigDTO to save.
     * @param tenantFeeConfigDTO the tenantFeeConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantFeeConfigDTO,
     * or with status {@code 400 (Bad Request)} if the tenantFeeConfigDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tenantFeeConfigDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenantFeeConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TenantFeeConfigDTO> partialUpdateTenantFeeConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TenantFeeConfigDTO tenantFeeConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TenantFeeConfig : {}, {}", id, tenantFeeConfigDTO);
        if (tenantFeeConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantFeeConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantFeeConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TenantFeeConfigDTO> result = tenantFeeConfigService.partialUpdate(tenantFeeConfigDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantFeeConfigDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tenant-fee-configs} : get all the Tenant Fee Configs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tenant Fee Configs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TenantFeeConfigDTO>> getAllTenantFeeConfigs(
        TenantFeeConfigCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TenantFeeConfigs by criteria: {}", criteria);

        Page<TenantFeeConfigDTO> page = tenantFeeConfigQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tenant-fee-configs/count} : count all the tenantFeeConfigs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTenantFeeConfigs(TenantFeeConfigCriteria criteria) {
        LOG.debug("REST request to count TenantFeeConfigs by criteria: {}", criteria);
        return ResponseEntity.ok().body(tenantFeeConfigQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tenant-fee-configs/:id} : get the "id" tenantFeeConfig.
     *
     * @param id the id of the tenantFeeConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenantFeeConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantFeeConfigDTO> getTenantFeeConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TenantFeeConfig : {}", id);
        Optional<TenantFeeConfigDTO> tenantFeeConfigDTO = tenantFeeConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tenantFeeConfigDTO);
    }

    /**
     * {@code DELETE  /tenant-fee-configs/:id} : delete the "id" tenantFeeConfig.
     *
     * @param id the id of the tenantFeeConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenantFeeConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TenantFeeConfig : {}", id);
        tenantFeeConfigService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
