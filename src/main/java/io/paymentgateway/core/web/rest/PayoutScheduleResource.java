package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.PayoutScheduleRepository;
import io.paymentgateway.core.service.PayoutScheduleQueryService;
import io.paymentgateway.core.service.PayoutScheduleService;
import io.paymentgateway.core.service.criteria.PayoutScheduleCriteria;
import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.PayoutSchedule}.
 */
@RestController
@RequestMapping("/api/payout-schedules")
public class PayoutScheduleResource {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutScheduleResource.class);

    private static final String ENTITY_NAME = "payoutSchedule";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final PayoutScheduleService payoutScheduleService;

    private final PayoutScheduleRepository payoutScheduleRepository;

    private final PayoutScheduleQueryService payoutScheduleQueryService;

    public PayoutScheduleResource(
        PayoutScheduleService payoutScheduleService,
        PayoutScheduleRepository payoutScheduleRepository,
        PayoutScheduleQueryService payoutScheduleQueryService
    ) {
        this.payoutScheduleService = payoutScheduleService;
        this.payoutScheduleRepository = payoutScheduleRepository;
        this.payoutScheduleQueryService = payoutScheduleQueryService;
    }

    /**
     * {@code POST  /payout-schedules} : Create a new payoutSchedule.
     *
     * @param payoutScheduleDTO the payoutScheduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new payoutScheduleDTO, or with status {@code 400 (Bad Request)} if the payoutSchedule already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PayoutScheduleDTO> createPayoutSchedule(@Valid @RequestBody PayoutScheduleDTO payoutScheduleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PayoutSchedule : {}", payoutScheduleDTO);
        if (payoutScheduleDTO.getId() != null) {
            throw new BadRequestAlertException("A new payoutSchedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        payoutScheduleDTO = payoutScheduleService.save(payoutScheduleDTO);
        return ResponseEntity.created(new URI("/api/payout-schedules/" + payoutScheduleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, payoutScheduleDTO.getId().toString()))
            .body(payoutScheduleDTO);
    }

    /**
     * {@code PUT  /payout-schedules/:id} : Updates an existing payoutSchedule.
     *
     * @param id the id of the payoutScheduleDTO to save.
     * @param payoutScheduleDTO the payoutScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payoutScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the payoutScheduleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the payoutScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PayoutScheduleDTO> updatePayoutSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PayoutScheduleDTO payoutScheduleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PayoutSchedule : {}, {}", id, payoutScheduleDTO);
        if (payoutScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payoutScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!payoutScheduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        payoutScheduleDTO = payoutScheduleService.update(payoutScheduleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, payoutScheduleDTO.getId().toString()))
            .body(payoutScheduleDTO);
    }

    /**
     * {@code PATCH  /payout-schedules/:id} : Partial updates given fields of an existing payoutSchedule, field will ignore if it is null
     *
     * @param id the id of the payoutScheduleDTO to save.
     * @param payoutScheduleDTO the payoutScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payoutScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the payoutScheduleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the payoutScheduleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the payoutScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PayoutScheduleDTO> partialUpdatePayoutSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PayoutScheduleDTO payoutScheduleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update PayoutSchedule : {}, {}", id, payoutScheduleDTO);
        if (payoutScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payoutScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!payoutScheduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PayoutScheduleDTO> result = payoutScheduleService.partialUpdate(payoutScheduleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, payoutScheduleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payout-schedules} : get all the Payout Schedules.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Payout Schedules in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PayoutScheduleDTO>> getAllPayoutSchedules(
        PayoutScheduleCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PayoutSchedules by criteria: {}", criteria);

        Page<PayoutScheduleDTO> page = payoutScheduleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payout-schedules/count} : count all the payoutSchedules.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPayoutSchedules(PayoutScheduleCriteria criteria) {
        LOG.debug("REST request to count PayoutSchedules by criteria: {}", criteria);
        return ResponseEntity.ok().body(payoutScheduleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /payout-schedules/:id} : get the "id" payoutSchedule.
     *
     * @param id the id of the payoutScheduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the payoutScheduleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PayoutScheduleDTO> getPayoutSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PayoutSchedule : {}", id);
        Optional<PayoutScheduleDTO> payoutScheduleDTO = payoutScheduleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(payoutScheduleDTO);
    }

    /**
     * {@code DELETE  /payout-schedules/:id} : delete the "id" payoutSchedule.
     *
     * @param id the id of the payoutScheduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayoutSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PayoutSchedule : {}", id);
        payoutScheduleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
