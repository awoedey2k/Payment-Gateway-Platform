package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.RoutingRuleRepository;
import io.paymentgateway.core.service.RoutingRuleQueryService;
import io.paymentgateway.core.service.RoutingRuleService;
import io.paymentgateway.core.service.criteria.RoutingRuleCriteria;
import io.paymentgateway.core.service.dto.RoutingRuleDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.RoutingRule}.
 */
@RestController
@RequestMapping("/api/routing-rules")
public class RoutingRuleResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingRuleResource.class);

    private static final String ENTITY_NAME = "routingRule";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final RoutingRuleService routingRuleService;

    private final RoutingRuleRepository routingRuleRepository;

    private final RoutingRuleQueryService routingRuleQueryService;

    public RoutingRuleResource(
        RoutingRuleService routingRuleService,
        RoutingRuleRepository routingRuleRepository,
        RoutingRuleQueryService routingRuleQueryService
    ) {
        this.routingRuleService = routingRuleService;
        this.routingRuleRepository = routingRuleRepository;
        this.routingRuleQueryService = routingRuleQueryService;
    }

    /**
     * {@code POST  /routing-rules} : Create a new routingRule.
     *
     * @param routingRuleDTO the routingRuleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new routingRuleDTO, or with status {@code 400 (Bad Request)} if the routingRule already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoutingRuleDTO> createRoutingRule(@Valid @RequestBody RoutingRuleDTO routingRuleDTO) throws URISyntaxException {
        LOG.debug("REST request to save RoutingRule : {}", routingRuleDTO);
        if (routingRuleDTO.getId() != null) {
            throw new BadRequestAlertException("A new routingRule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        routingRuleDTO = routingRuleService.save(routingRuleDTO);
        return ResponseEntity.created(new URI("/api/routing-rules/" + routingRuleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, routingRuleDTO.getId().toString()))
            .body(routingRuleDTO);
    }

    /**
     * {@code PUT  /routing-rules/:id} : Updates an existing routingRule.
     *
     * @param id the id of the routingRuleDTO to save.
     * @param routingRuleDTO the routingRuleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routingRuleDTO,
     * or with status {@code 400 (Bad Request)} if the routingRuleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the routingRuleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoutingRuleDTO> updateRoutingRule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoutingRuleDTO routingRuleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RoutingRule : {}, {}", id, routingRuleDTO);
        if (routingRuleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routingRuleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routingRuleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        routingRuleDTO = routingRuleService.update(routingRuleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, routingRuleDTO.getId().toString()))
            .body(routingRuleDTO);
    }

    /**
     * {@code PATCH  /routing-rules/:id} : Partial updates given fields of an existing routingRule, field will ignore if it is null
     *
     * @param id the id of the routingRuleDTO to save.
     * @param routingRuleDTO the routingRuleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routingRuleDTO,
     * or with status {@code 400 (Bad Request)} if the routingRuleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the routingRuleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the routingRuleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoutingRuleDTO> partialUpdateRoutingRule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoutingRuleDTO routingRuleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update RoutingRule : {}, {}", id, routingRuleDTO);
        if (routingRuleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routingRuleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routingRuleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoutingRuleDTO> result = routingRuleService.partialUpdate(routingRuleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, routingRuleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /routing-rules} : get all the Routing Rules.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Routing Rules in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RoutingRuleDTO>> getAllRoutingRules(
        RoutingRuleCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RoutingRules by criteria: {}", criteria);

        Page<RoutingRuleDTO> page = routingRuleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /routing-rules/count} : count all the routingRules.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRoutingRules(RoutingRuleCriteria criteria) {
        LOG.debug("REST request to count RoutingRules by criteria: {}", criteria);
        return ResponseEntity.ok().body(routingRuleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /routing-rules/:id} : get the "id" routingRule.
     *
     * @param id the id of the routingRuleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the routingRuleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoutingRuleDTO> getRoutingRule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RoutingRule : {}", id);
        Optional<RoutingRuleDTO> routingRuleDTO = routingRuleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(routingRuleDTO);
    }

    /**
     * {@code DELETE  /routing-rules/:id} : delete the "id" routingRule.
     *
     * @param id the id of the routingRuleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutingRule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RoutingRule : {}", id);
        routingRuleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
