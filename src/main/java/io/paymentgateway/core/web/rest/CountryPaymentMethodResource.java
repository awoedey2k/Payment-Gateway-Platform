package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.CountryPaymentMethodRepository;
import io.paymentgateway.core.service.CountryPaymentMethodQueryService;
import io.paymentgateway.core.service.CountryPaymentMethodService;
import io.paymentgateway.core.service.criteria.CountryPaymentMethodCriteria;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.CountryPaymentMethod}.
 */
@RestController
@RequestMapping("/api/country-payment-methods")
public class CountryPaymentMethodResource {

    private static final Logger LOG = LoggerFactory.getLogger(CountryPaymentMethodResource.class);

    private static final String ENTITY_NAME = "countryPaymentMethod";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final CountryPaymentMethodService countryPaymentMethodService;

    private final CountryPaymentMethodRepository countryPaymentMethodRepository;

    private final CountryPaymentMethodQueryService countryPaymentMethodQueryService;

    public CountryPaymentMethodResource(
        CountryPaymentMethodService countryPaymentMethodService,
        CountryPaymentMethodRepository countryPaymentMethodRepository,
        CountryPaymentMethodQueryService countryPaymentMethodQueryService
    ) {
        this.countryPaymentMethodService = countryPaymentMethodService;
        this.countryPaymentMethodRepository = countryPaymentMethodRepository;
        this.countryPaymentMethodQueryService = countryPaymentMethodQueryService;
    }

    /**
     * {@code POST  /country-payment-methods} : Create a new countryPaymentMethod.
     *
     * @param countryPaymentMethodDTO the countryPaymentMethodDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new countryPaymentMethodDTO, or with status {@code 400 (Bad Request)} if the countryPaymentMethod already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CountryPaymentMethodDTO> createCountryPaymentMethod(
        @Valid @RequestBody CountryPaymentMethodDTO countryPaymentMethodDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CountryPaymentMethod : {}", countryPaymentMethodDTO);
        if (countryPaymentMethodDTO.getId() != null) {
            throw new BadRequestAlertException("A new countryPaymentMethod cannot already have an ID", ENTITY_NAME, "idexists");
        }
        countryPaymentMethodDTO = countryPaymentMethodService.save(countryPaymentMethodDTO);
        return ResponseEntity.created(new URI("/api/country-payment-methods/" + countryPaymentMethodDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, countryPaymentMethodDTO.getId().toString()))
            .body(countryPaymentMethodDTO);
    }

    /**
     * {@code PUT  /country-payment-methods/:id} : Updates an existing countryPaymentMethod.
     *
     * @param id the id of the countryPaymentMethodDTO to save.
     * @param countryPaymentMethodDTO the countryPaymentMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryPaymentMethodDTO,
     * or with status {@code 400 (Bad Request)} if the countryPaymentMethodDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the countryPaymentMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CountryPaymentMethodDTO> updateCountryPaymentMethod(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CountryPaymentMethodDTO countryPaymentMethodDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CountryPaymentMethod : {}, {}", id, countryPaymentMethodDTO);
        if (countryPaymentMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryPaymentMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryPaymentMethodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        countryPaymentMethodDTO = countryPaymentMethodService.update(countryPaymentMethodDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, countryPaymentMethodDTO.getId().toString()))
            .body(countryPaymentMethodDTO);
    }

    /**
     * {@code PATCH  /country-payment-methods/:id} : Partial updates given fields of an existing countryPaymentMethod, field will ignore if it is null
     *
     * @param id the id of the countryPaymentMethodDTO to save.
     * @param countryPaymentMethodDTO the countryPaymentMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryPaymentMethodDTO,
     * or with status {@code 400 (Bad Request)} if the countryPaymentMethodDTO is not valid,
     * or with status {@code 404 (Not Found)} if the countryPaymentMethodDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the countryPaymentMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CountryPaymentMethodDTO> partialUpdateCountryPaymentMethod(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CountryPaymentMethodDTO countryPaymentMethodDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CountryPaymentMethod : {}, {}", id, countryPaymentMethodDTO);
        if (countryPaymentMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryPaymentMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryPaymentMethodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CountryPaymentMethodDTO> result = countryPaymentMethodService.partialUpdate(countryPaymentMethodDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, countryPaymentMethodDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /country-payment-methods} : get all the Country Payment Methods.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Country Payment Methods in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CountryPaymentMethodDTO>> getAllCountryPaymentMethods(
        CountryPaymentMethodCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CountryPaymentMethods by criteria: {}", criteria);

        Page<CountryPaymentMethodDTO> page = countryPaymentMethodQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /country-payment-methods/count} : count all the countryPaymentMethods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCountryPaymentMethods(CountryPaymentMethodCriteria criteria) {
        LOG.debug("REST request to count CountryPaymentMethods by criteria: {}", criteria);
        return ResponseEntity.ok().body(countryPaymentMethodQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /country-payment-methods/:id} : get the "id" countryPaymentMethod.
     *
     * @param id the id of the countryPaymentMethodDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the countryPaymentMethodDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CountryPaymentMethodDTO> getCountryPaymentMethod(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CountryPaymentMethod : {}", id);
        Optional<CountryPaymentMethodDTO> countryPaymentMethodDTO = countryPaymentMethodService.findOne(id);
        return ResponseUtil.wrapOrNotFound(countryPaymentMethodDTO);
    }

    /**
     * {@code DELETE  /country-payment-methods/:id} : delete the "id" countryPaymentMethod.
     *
     * @param id the id of the countryPaymentMethodDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountryPaymentMethod(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CountryPaymentMethod : {}", id);
        countryPaymentMethodService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
