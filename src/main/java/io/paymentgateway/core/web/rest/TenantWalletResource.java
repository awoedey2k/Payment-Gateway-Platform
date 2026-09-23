package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.TenantWalletRepository;
import io.paymentgateway.core.service.TenantWalletQueryService;
import io.paymentgateway.core.service.TenantWalletService;
import io.paymentgateway.core.service.criteria.TenantWalletCriteria;
import io.paymentgateway.core.service.dto.TenantWalletDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.TenantWallet}.
 */
@RestController
@RequestMapping("/api/tenant-wallets")
public class TenantWalletResource {

    private static final Logger LOG = LoggerFactory.getLogger(TenantWalletResource.class);

    private static final String ENTITY_NAME = "tenantWallet";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final TenantWalletService tenantWalletService;

    private final TenantWalletRepository tenantWalletRepository;

    private final TenantWalletQueryService tenantWalletQueryService;

    public TenantWalletResource(
        TenantWalletService tenantWalletService,
        TenantWalletRepository tenantWalletRepository,
        TenantWalletQueryService tenantWalletQueryService
    ) {
        this.tenantWalletService = tenantWalletService;
        this.tenantWalletRepository = tenantWalletRepository;
        this.tenantWalletQueryService = tenantWalletQueryService;
    }

    /**
     * {@code POST  /tenant-wallets} : Create a new tenantWallet.
     *
     * @param tenantWalletDTO the tenantWalletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenantWalletDTO, or with status {@code 400 (Bad Request)} if the tenantWallet already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TenantWalletDTO> createTenantWallet(@Valid @RequestBody TenantWalletDTO tenantWalletDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TenantWallet : {}", tenantWalletDTO);
        if (tenantWalletDTO.getId() != null) {
            throw new BadRequestAlertException("A new tenantWallet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tenantWalletDTO = tenantWalletService.save(tenantWalletDTO);
        return ResponseEntity.created(new URI("/api/tenant-wallets/" + tenantWalletDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tenantWalletDTO.getId().toString()))
            .body(tenantWalletDTO);
    }

    /**
     * {@code PUT  /tenant-wallets/:id} : Updates an existing tenantWallet.
     *
     * @param id the id of the tenantWalletDTO to save.
     * @param tenantWalletDTO the tenantWalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantWalletDTO,
     * or with status {@code 400 (Bad Request)} if the tenantWalletDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenantWalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantWalletDTO> updateTenantWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TenantWalletDTO tenantWalletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TenantWallet : {}, {}", id, tenantWalletDTO);
        if (tenantWalletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantWalletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantWalletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tenantWalletDTO = tenantWalletService.update(tenantWalletDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantWalletDTO.getId().toString()))
            .body(tenantWalletDTO);
    }

    /**
     * {@code PATCH  /tenant-wallets/:id} : Partial updates given fields of an existing tenantWallet, field will ignore if it is null
     *
     * @param id the id of the tenantWalletDTO to save.
     * @param tenantWalletDTO the tenantWalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenantWalletDTO,
     * or with status {@code 400 (Bad Request)} if the tenantWalletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tenantWalletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenantWalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TenantWalletDTO> partialUpdateTenantWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TenantWalletDTO tenantWalletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TenantWallet : {}, {}", id, tenantWalletDTO);
        if (tenantWalletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenantWalletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantWalletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TenantWalletDTO> result = tenantWalletService.partialUpdate(tenantWalletDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tenantWalletDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tenant-wallets} : get all the Tenant Wallets.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tenant Wallets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TenantWalletDTO>> getAllTenantWallets(
        TenantWalletCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TenantWallets by criteria: {}", criteria);

        Page<TenantWalletDTO> page = tenantWalletQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tenant-wallets/count} : count all the tenantWallets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTenantWallets(TenantWalletCriteria criteria) {
        LOG.debug("REST request to count TenantWallets by criteria: {}", criteria);
        return ResponseEntity.ok().body(tenantWalletQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tenant-wallets/:id} : get the "id" tenantWallet.
     *
     * @param id the id of the tenantWalletDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenantWalletDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantWalletDTO> getTenantWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TenantWallet : {}", id);
        Optional<TenantWalletDTO> tenantWalletDTO = tenantWalletService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tenantWalletDTO);
    }

    /**
     * {@code DELETE  /tenant-wallets/:id} : delete the "id" tenantWallet.
     *
     * @param id the id of the tenantWalletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenantWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TenantWallet : {}", id);
        tenantWalletService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
