package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.WebhookDeliveryAttemptRepository;
import io.paymentgateway.core.service.WebhookDeliveryAttemptQueryService;
import io.paymentgateway.core.service.WebhookDeliveryAttemptService;
import io.paymentgateway.core.service.criteria.WebhookDeliveryAttemptCriteria;
import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.WebhookDeliveryAttempt}.
 */
@RestController
@RequestMapping("/api/webhook-delivery-attempts")
public class WebhookDeliveryAttemptResource {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryAttemptResource.class);

    private static final String ENTITY_NAME = "webhookDeliveryAttempt";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final WebhookDeliveryAttemptService webhookDeliveryAttemptService;

    private final WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository;

    private final WebhookDeliveryAttemptQueryService webhookDeliveryAttemptQueryService;

    public WebhookDeliveryAttemptResource(
        WebhookDeliveryAttemptService webhookDeliveryAttemptService,
        WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository,
        WebhookDeliveryAttemptQueryService webhookDeliveryAttemptQueryService
    ) {
        this.webhookDeliveryAttemptService = webhookDeliveryAttemptService;
        this.webhookDeliveryAttemptRepository = webhookDeliveryAttemptRepository;
        this.webhookDeliveryAttemptQueryService = webhookDeliveryAttemptQueryService;
    }

    /**
     * {@code POST  /webhook-delivery-attempts} : Create a new webhookDeliveryAttempt.
     *
     * @param webhookDeliveryAttemptDTO the webhookDeliveryAttemptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new webhookDeliveryAttemptDTO, or with status {@code 400 (Bad Request)} if the webhookDeliveryAttempt already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WebhookDeliveryAttemptDTO> createWebhookDeliveryAttempt(
        @Valid @RequestBody WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save WebhookDeliveryAttempt : {}", webhookDeliveryAttemptDTO);
        if (webhookDeliveryAttemptDTO.getId() != null) {
            throw new BadRequestAlertException("A new webhookDeliveryAttempt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        webhookDeliveryAttemptDTO = webhookDeliveryAttemptService.save(webhookDeliveryAttemptDTO);
        return ResponseEntity.created(new URI("/api/webhook-delivery-attempts/" + webhookDeliveryAttemptDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, webhookDeliveryAttemptDTO.getId().toString())
            )
            .body(webhookDeliveryAttemptDTO);
    }

    /**
     * {@code PUT  /webhook-delivery-attempts/:id} : Updates an existing webhookDeliveryAttempt.
     *
     * @param id the id of the webhookDeliveryAttemptDTO to save.
     * @param webhookDeliveryAttemptDTO the webhookDeliveryAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookDeliveryAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the webhookDeliveryAttemptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the webhookDeliveryAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WebhookDeliveryAttemptDTO> updateWebhookDeliveryAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WebhookDeliveryAttempt : {}, {}", id, webhookDeliveryAttemptDTO);
        if (webhookDeliveryAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookDeliveryAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookDeliveryAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        webhookDeliveryAttemptDTO = webhookDeliveryAttemptService.update(webhookDeliveryAttemptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, webhookDeliveryAttemptDTO.getId().toString()))
            .body(webhookDeliveryAttemptDTO);
    }

    /**
     * {@code PATCH  /webhook-delivery-attempts/:id} : Partial updates given fields of an existing webhookDeliveryAttempt, field will ignore if it is null
     *
     * @param id the id of the webhookDeliveryAttemptDTO to save.
     * @param webhookDeliveryAttemptDTO the webhookDeliveryAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookDeliveryAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the webhookDeliveryAttemptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the webhookDeliveryAttemptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the webhookDeliveryAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WebhookDeliveryAttemptDTO> partialUpdateWebhookDeliveryAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WebhookDeliveryAttempt : {}, {}", id, webhookDeliveryAttemptDTO);
        if (webhookDeliveryAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookDeliveryAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookDeliveryAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WebhookDeliveryAttemptDTO> result = webhookDeliveryAttemptService.partialUpdate(webhookDeliveryAttemptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, webhookDeliveryAttemptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /webhook-delivery-attempts} : get all the Webhook Delivery Attempts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Webhook Delivery Attempts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WebhookDeliveryAttemptDTO>> getAllWebhookDeliveryAttempts(
        WebhookDeliveryAttemptCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WebhookDeliveryAttempts by criteria: {}", criteria);

        Page<WebhookDeliveryAttemptDTO> page = webhookDeliveryAttemptQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /webhook-delivery-attempts/count} : count all the webhookDeliveryAttempts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWebhookDeliveryAttempts(WebhookDeliveryAttemptCriteria criteria) {
        LOG.debug("REST request to count WebhookDeliveryAttempts by criteria: {}", criteria);
        return ResponseEntity.ok().body(webhookDeliveryAttemptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /webhook-delivery-attempts/:id} : get the "id" webhookDeliveryAttempt.
     *
     * @param id the id of the webhookDeliveryAttemptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the webhookDeliveryAttemptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebhookDeliveryAttemptDTO> getWebhookDeliveryAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WebhookDeliveryAttempt : {}", id);
        Optional<WebhookDeliveryAttemptDTO> webhookDeliveryAttemptDTO = webhookDeliveryAttemptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(webhookDeliveryAttemptDTO);
    }

    /**
     * {@code DELETE  /webhook-delivery-attempts/:id} : delete the "id" webhookDeliveryAttempt.
     *
     * @param id the id of the webhookDeliveryAttemptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhookDeliveryAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WebhookDeliveryAttempt : {}", id);
        webhookDeliveryAttemptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
