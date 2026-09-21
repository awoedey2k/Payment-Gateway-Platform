package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.CurrencyRepository;
import io.paymentgateway.core.service.CurrencyQueryService;
import io.paymentgateway.core.service.CurrencyService;
import io.paymentgateway.core.service.criteria.CurrencyCriteria;
import io.paymentgateway.core.service.dto.CurrencyDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.Currency}.
 */
@RestController
@RequestMapping("/api/currencies")
public class CurrencyResource {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyResource.class);

    private static final String ENTITY_NAME = "currency";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final CurrencyService currencyService;

    private final CurrencyRepository currencyRepository;

    private final CurrencyQueryService currencyQueryService;

    public CurrencyResource(
        CurrencyService currencyService,
        CurrencyRepository currencyRepository,
        CurrencyQueryService currencyQueryService
    ) {
        this.currencyService = currencyService;
        this.currencyRepository = currencyRepository;
        this.currencyQueryService = currencyQueryService;
    }

    /**
     * {@code POST  /currencies} : Create a new currency.
     *
     * @param currencyDTO the currencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new currencyDTO, or with status {@code 400 (Bad Request)} if the currency already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CurrencyDTO> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Currency : {}", currencyDTO);
        if (currencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new currency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        currencyDTO = currencyService.save(currencyDTO);
        return ResponseEntity.created(new URI("/api/currencies/" + currencyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, currencyDTO.getId().toString()))
            .body(currencyDTO);
    }

    /**
     * {@code PUT  /currencies/:id} : Updates an existing currency.
     *
     * @param id the id of the currencyDTO to save.
     * @param currencyDTO the currencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated currencyDTO,
     * or with status {@code 400 (Bad Request)} if the currencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the currencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CurrencyDTO> updateCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CurrencyDTO currencyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Currency : {}, {}", id, currencyDTO);
        if (currencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, currencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!currencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        currencyDTO = currencyService.update(currencyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, currencyDTO.getId().toString()))
            .body(currencyDTO);
    }

    /**
     * {@code PATCH  /currencies/:id} : Partial updates given fields of an existing currency, field will ignore if it is null
     *
     * @param id the id of the currencyDTO to save.
     * @param currencyDTO the currencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated currencyDTO,
     * or with status {@code 400 (Bad Request)} if the currencyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the currencyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the currencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CurrencyDTO> partialUpdateCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CurrencyDTO currencyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Currency : {}, {}", id, currencyDTO);
        if (currencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, currencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!currencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CurrencyDTO> result = currencyService.partialUpdate(currencyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, currencyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /currencies} : get all the Currencies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Currencies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies(
        CurrencyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Currencies by criteria: {}", criteria);

        Page<CurrencyDTO> page = currencyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /currencies/count} : count all the currencies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCurrencies(CurrencyCriteria criteria) {
        LOG.debug("REST request to count Currencies by criteria: {}", criteria);
        return ResponseEntity.ok().body(currencyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /currencies/:id} : get the "id" currency.
     *
     * @param id the id of the currencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the currencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CurrencyDTO> getCurrency(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Currency : {}", id);
        Optional<CurrencyDTO> currencyDTO = currencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(currencyDTO);
    }

    /**
     * {@code DELETE  /currencies/:id} : delete the "id" currency.
     *
     * @param id the id of the currencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Currency : {}", id);
        currencyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
