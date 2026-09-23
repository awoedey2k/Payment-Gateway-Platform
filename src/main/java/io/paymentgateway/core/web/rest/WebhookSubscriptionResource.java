package io.paymentgateway.core.web.rest;

import io.paymentgateway.core.repository.WebhookSubscriptionRepository;
import io.paymentgateway.core.service.WebhookSubscriptionQueryService;
import io.paymentgateway.core.service.WebhookSubscriptionService;
import io.paymentgateway.core.service.criteria.WebhookSubscriptionCriteria;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
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
 * REST controller for managing {@link io.paymentgateway.core.domain.WebhookSubscription}.
 */
@RestController
@RequestMapping("/api/webhook-subscriptions")
public class WebhookSubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookSubscriptionResource.class);

    private static final String ENTITY_NAME = "webhookSubscription";

    @Value("${jhipster.clientApp.name:paymentgateway}")
    private String applicationName;

    private final WebhookSubscriptionService webhookSubscriptionService;

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    private final WebhookSubscriptionQueryService webhookSubscriptionQueryService;

    public WebhookSubscriptionResource(
        WebhookSubscriptionService webhookSubscriptionService,
        WebhookSubscriptionRepository webhookSubscriptionRepository,
        WebhookSubscriptionQueryService webhookSubscriptionQueryService
    ) {
        this.webhookSubscriptionService = webhookSubscriptionService;
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.webhookSubscriptionQueryService = webhookSubscriptionQueryService;
    }

    /**
     * {@code POST  /webhook-subscriptions} : Create a new webhookSubscription.
     *
     * @param webhookSubscriptionDTO the webhookSubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new webhookSubscriptionDTO, or with status {@code 400 (Bad Request)} if the webhookSubscription already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WebhookSubscriptionDTO> createWebhookSubscription(
        @Valid @RequestBody WebhookSubscriptionDTO webhookSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save WebhookSubscription : {}", webhookSubscriptionDTO);
        if (webhookSubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new webhookSubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        webhookSubscriptionDTO = webhookSubscriptionService.save(webhookSubscriptionDTO);
        return ResponseEntity.created(new URI("/api/webhook-subscriptions/" + webhookSubscriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, webhookSubscriptionDTO.getId().toString()))
            .body(webhookSubscriptionDTO);
    }

    /**
     * {@code PUT  /webhook-subscriptions/:id} : Updates an existing webhookSubscription.
     *
     * @param id the id of the webhookSubscriptionDTO to save.
     * @param webhookSubscriptionDTO the webhookSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the webhookSubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the webhookSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WebhookSubscriptionDTO> updateWebhookSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WebhookSubscriptionDTO webhookSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WebhookSubscription : {}, {}", id, webhookSubscriptionDTO);
        if (webhookSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookSubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        webhookSubscriptionDTO = webhookSubscriptionService.update(webhookSubscriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, webhookSubscriptionDTO.getId().toString()))
            .body(webhookSubscriptionDTO);
    }

    /**
     * {@code PATCH  /webhook-subscriptions/:id} : Partial updates given fields of an existing webhookSubscription, field will ignore if it is null
     *
     * @param id the id of the webhookSubscriptionDTO to save.
     * @param webhookSubscriptionDTO the webhookSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the webhookSubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the webhookSubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the webhookSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WebhookSubscriptionDTO> partialUpdateWebhookSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WebhookSubscriptionDTO webhookSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WebhookSubscription : {}, {}", id, webhookSubscriptionDTO);
        if (webhookSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookSubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WebhookSubscriptionDTO> result = webhookSubscriptionService.partialUpdate(webhookSubscriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, webhookSubscriptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /webhook-subscriptions} : get all the Webhook Subscriptions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Webhook Subscriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WebhookSubscriptionDTO>> getAllWebhookSubscriptions(
        WebhookSubscriptionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WebhookSubscriptions by criteria: {}", criteria);

        Page<WebhookSubscriptionDTO> page = webhookSubscriptionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /webhook-subscriptions/count} : count all the webhookSubscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWebhookSubscriptions(WebhookSubscriptionCriteria criteria) {
        LOG.debug("REST request to count WebhookSubscriptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(webhookSubscriptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /webhook-subscriptions/:id} : get the "id" webhookSubscription.
     *
     * @param id the id of the webhookSubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the webhookSubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebhookSubscriptionDTO> getWebhookSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WebhookSubscription : {}", id);
        Optional<WebhookSubscriptionDTO> webhookSubscriptionDTO = webhookSubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(webhookSubscriptionDTO);
    }

    /**
     * {@code DELETE  /webhook-subscriptions/:id} : delete the "id" webhookSubscription.
     *
     * @param id the id of the webhookSubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhookSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WebhookSubscription : {}", id);
        webhookSubscriptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
