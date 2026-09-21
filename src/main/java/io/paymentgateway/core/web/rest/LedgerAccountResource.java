package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.LedgerAccountRepository;
import io.paymentgateway.core.service.LedgerAccountQueryService;
import io.paymentgateway.core.service.LedgerAccountService;
import io.paymentgateway.core.service.criteria.LedgerAccountCriteria;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.LedgerAccount}.
 */
@RestController
@RequestMapping("/api/ledger-accounts")
public class LedgerAccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(LedgerAccountResource.class);

    private static final String ENTITY_NAME = "ledgerAccount";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final LedgerAccountService ledgerAccountService;

    private final LedgerAccountRepository ledgerAccountRepository;

    private final LedgerAccountQueryService ledgerAccountQueryService;

    public LedgerAccountResource(
        LedgerAccountService ledgerAccountService,
        LedgerAccountRepository ledgerAccountRepository,
        LedgerAccountQueryService ledgerAccountQueryService
    ) {
        this.ledgerAccountService = ledgerAccountService;
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.ledgerAccountQueryService = ledgerAccountQueryService;
    }

    /**
     * {@code POST  /ledger-accounts} : Create a new ledgerAccount.
     *
     * @param ledgerAccountDTO the ledgerAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ledgerAccountDTO, or with status {@code 400 (Bad Request)} if the ledgerAccount already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LedgerAccountDTO> createLedgerAccount(@Valid @RequestBody LedgerAccountDTO ledgerAccountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LedgerAccount : {}", ledgerAccountDTO);
        if (ledgerAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new ledgerAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ledgerAccountDTO = ledgerAccountService.save(ledgerAccountDTO);
        return ResponseEntity.created(new URI("/api/ledger-accounts/" + ledgerAccountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, ledgerAccountDTO.getId().toString()))
            .body(ledgerAccountDTO);
    }

    /**
     * {@code PUT  /ledger-accounts/:id} : Updates an existing ledgerAccount.
     *
     * @param id the id of the ledgerAccountDTO to save.
     * @param ledgerAccountDTO the ledgerAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ledgerAccountDTO,
     * or with status {@code 400 (Bad Request)} if the ledgerAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ledgerAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LedgerAccountDTO> updateLedgerAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LedgerAccountDTO ledgerAccountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LedgerAccount : {}, {}", id, ledgerAccountDTO);
        if (ledgerAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ledgerAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ledgerAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ledgerAccountDTO = ledgerAccountService.update(ledgerAccountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ledgerAccountDTO.getId().toString()))
            .body(ledgerAccountDTO);
    }

    /**
     * {@code PATCH  /ledger-accounts/:id} : Partial updates given fields of an existing ledgerAccount, field will ignore if it is null
     *
     * @param id the id of the ledgerAccountDTO to save.
     * @param ledgerAccountDTO the ledgerAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ledgerAccountDTO,
     * or with status {@code 400 (Bad Request)} if the ledgerAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ledgerAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ledgerAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LedgerAccountDTO> partialUpdateLedgerAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LedgerAccountDTO ledgerAccountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update LedgerAccount : {}, {}", id, ledgerAccountDTO);
        if (ledgerAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ledgerAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ledgerAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LedgerAccountDTO> result = ledgerAccountService.partialUpdate(ledgerAccountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ledgerAccountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ledger-accounts} : get all the Ledger Accounts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ledger Accounts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LedgerAccountDTO>> getAllLedgerAccounts(
        LedgerAccountCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LedgerAccounts by criteria: {}", criteria);

        Page<LedgerAccountDTO> page = ledgerAccountQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ledger-accounts/count} : count all the ledgerAccounts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLedgerAccounts(LedgerAccountCriteria criteria) {
        LOG.debug("REST request to count LedgerAccounts by criteria: {}", criteria);
        return ResponseEntity.ok().body(ledgerAccountQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ledger-accounts/:id} : get the "id" ledgerAccount.
     *
     * @param id the id of the ledgerAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ledgerAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LedgerAccountDTO> getLedgerAccount(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LedgerAccount : {}", id);
        Optional<LedgerAccountDTO> ledgerAccountDTO = ledgerAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ledgerAccountDTO);
    }

    /**
     * {@code DELETE  /ledger-accounts/:id} : delete the "id" ledgerAccount.
     *
     * @param id the id of the ledgerAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLedgerAccount(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LedgerAccount : {}", id);
        ledgerAccountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
