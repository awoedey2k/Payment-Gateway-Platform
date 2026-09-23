package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.ForexRateRepository;
import io.paymentgateway.core.service.ForexRateQueryService;
import io.paymentgateway.core.service.ForexRateService;
import io.paymentgateway.core.service.criteria.ForexRateCriteria;
import io.paymentgateway.core.service.dto.ForexRateDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.ForexRate}.
 */
@RestController
@RequestMapping("/api/forex-rates")
public class ForexRateResource {

    private static final Logger LOG = LoggerFactory.getLogger(ForexRateResource.class);

    private static final String ENTITY_NAME = "forexRate";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final ForexRateService forexRateService;

    private final ForexRateRepository forexRateRepository;

    private final ForexRateQueryService forexRateQueryService;

    public ForexRateResource(
        ForexRateService forexRateService,
        ForexRateRepository forexRateRepository,
        ForexRateQueryService forexRateQueryService
    ) {
        this.forexRateService = forexRateService;
        this.forexRateRepository = forexRateRepository;
        this.forexRateQueryService = forexRateQueryService;
    }

    /**
     * {@code POST  /forex-rates} : Create a new forexRate.
     *
     * @param forexRateDTO the forexRateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new forexRateDTO, or with status {@code 400 (Bad Request)} if the forexRate already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ForexRateDTO> createForexRate(@Valid @RequestBody ForexRateDTO forexRateDTO) throws URISyntaxException {
        LOG.debug("REST request to save ForexRate : {}", forexRateDTO);
        if (forexRateDTO.getId() != null) {
            throw new BadRequestAlertException("A new forexRate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        forexRateDTO = forexRateService.save(forexRateDTO);
        return ResponseEntity.created(new URI("/api/forex-rates/" + forexRateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, forexRateDTO.getId().toString()))
            .body(forexRateDTO);
    }

    /**
     * {@code PUT  /forex-rates/:id} : Updates an existing forexRate.
     *
     * @param id the id of the forexRateDTO to save.
     * @param forexRateDTO the forexRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated forexRateDTO,
     * or with status {@code 400 (Bad Request)} if the forexRateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the forexRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ForexRateDTO> updateForexRate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ForexRateDTO forexRateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ForexRate : {}, {}", id, forexRateDTO);
        if (forexRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, forexRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!forexRateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        forexRateDTO = forexRateService.update(forexRateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, forexRateDTO.getId().toString()))
            .body(forexRateDTO);
    }

    /**
     * {@code PATCH  /forex-rates/:id} : Partial updates given fields of an existing forexRate, field will ignore if it is null
     *
     * @param id the id of the forexRateDTO to save.
     * @param forexRateDTO the forexRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated forexRateDTO,
     * or with status {@code 400 (Bad Request)} if the forexRateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the forexRateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the forexRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ForexRateDTO> partialUpdateForexRate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ForexRateDTO forexRateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ForexRate : {}, {}", id, forexRateDTO);
        if (forexRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, forexRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!forexRateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ForexRateDTO> result = forexRateService.partialUpdate(forexRateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, forexRateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /forex-rates} : get all the Forex Rates.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Forex Rates in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ForexRateDTO>> getAllForexRates(
        ForexRateCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ForexRates by criteria: {}", criteria);

        Page<ForexRateDTO> page = forexRateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /forex-rates/count} : count all the forexRates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countForexRates(ForexRateCriteria criteria) {
        LOG.debug("REST request to count ForexRates by criteria: {}", criteria);
        return ResponseEntity.ok().body(forexRateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /forex-rates/:id} : get the "id" forexRate.
     *
     * @param id the id of the forexRateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the forexRateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ForexRateDTO> getForexRate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ForexRate : {}", id);
        Optional<ForexRateDTO> forexRateDTO = forexRateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(forexRateDTO);
    }

    /**
     * {@code DELETE  /forex-rates/:id} : delete the "id" forexRate.
     *
     * @param id the id of the forexRateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForexRate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ForexRate : {}", id);
        forexRateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
