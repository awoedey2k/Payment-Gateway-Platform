package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.CountryRepository;
import io.paymentgateway.core.service.CountryQueryService;
import io.paymentgateway.core.service.CountryService;
import io.paymentgateway.core.service.criteria.CountryCriteria;
import io.paymentgateway.core.service.dto.CountryDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.Country}.
 */
@RestController
@RequestMapping("/api/countries")
public class CountryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResource.class);

    private static final String ENTITY_NAME = "country";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final CountryService countryService;

    private final CountryRepository countryRepository;

    private final CountryQueryService countryQueryService;

    public CountryResource(CountryService countryService, CountryRepository countryRepository, CountryQueryService countryQueryService) {
        this.countryService = countryService;
        this.countryRepository = countryRepository;
        this.countryQueryService = countryQueryService;
    }

    /**
     * {@code POST  /countries} : Create a new country.
     *
     * @param countryDTO the countryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new countryDTO, or with status {@code 400 (Bad Request)} if the country already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO countryDTO) throws URISyntaxException {
        LOG.debug("REST request to save Country : {}", countryDTO);
        if (countryDTO.getId() != null) {
            throw new BadRequestAlertException("A new country cannot already have an ID", ENTITY_NAME, "idexists");
        }
        countryDTO = countryService.save(countryDTO);
        return ResponseEntity.created(new URI("/api/countries/" + countryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, countryDTO.getId().toString()))
            .body(countryDTO);
    }

    /**
     * {@code PUT  /countries/:id} : Updates an existing country.
     *
     * @param id the id of the countryDTO to save.
     * @param countryDTO the countryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryDTO,
     * or with status {@code 400 (Bad Request)} if the countryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the countryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CountryDTO> updateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CountryDTO countryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Country : {}, {}", id, countryDTO);
        if (countryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        countryDTO = countryService.update(countryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, countryDTO.getId().toString()))
            .body(countryDTO);
    }

    /**
     * {@code PATCH  /countries/:id} : Partial updates given fields of an existing country, field will ignore if it is null
     *
     * @param id the id of the countryDTO to save.
     * @param countryDTO the countryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryDTO,
     * or with status {@code 400 (Bad Request)} if the countryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the countryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the countryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CountryDTO> partialUpdateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CountryDTO countryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Country : {}, {}", id, countryDTO);
        if (countryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CountryDTO> result = countryService.partialUpdate(countryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, countryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /countries} : get all the Countries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Countries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CountryDTO>> getAllCountries(
        CountryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Countries by criteria: {}", criteria);

        Page<CountryDTO> page = countryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /countries/count} : count all the countries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCountries(CountryCriteria criteria) {
        LOG.debug("REST request to count Countries by criteria: {}", criteria);
        return ResponseEntity.ok().body(countryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /countries/:id} : get the "id" country.
     *
     * @param id the id of the countryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the countryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CountryDTO> getCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Country : {}", id);
        Optional<CountryDTO> countryDTO = countryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(countryDTO);
    }

    /**
     * {@code DELETE  /countries/:id} : delete the "id" country.
     *
     * @param id the id of the countryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Country : {}", id);
        countryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
