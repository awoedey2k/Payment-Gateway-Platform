package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.SettlementBatch;
import io.paymentgateway.core.repository.SettlementBatchRepository;
import io.paymentgateway.core.service.criteria.SettlementBatchCriteria;
import io.paymentgateway.core.service.dto.SettlementBatchDTO;
import io.paymentgateway.core.service.mapper.SettlementBatchMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SettlementBatch} entities in the database.
 * The main input is a {@link SettlementBatchCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SettlementBatchDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SettlementBatchQueryService extends QueryService<SettlementBatch> {

    private static final Logger LOG = LoggerFactory.getLogger(SettlementBatchQueryService.class);

    private final SettlementBatchRepository settlementBatchRepository;

    private final SettlementBatchMapper settlementBatchMapper;

    public SettlementBatchQueryService(SettlementBatchRepository settlementBatchRepository, SettlementBatchMapper settlementBatchMapper) {
        this.settlementBatchRepository = settlementBatchRepository;
        this.settlementBatchMapper = settlementBatchMapper;
    }

    /**
     * Return a {@link Page} of {@link SettlementBatchDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SettlementBatchDTO> findByCriteria(SettlementBatchCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SettlementBatch> specification = createSpecification(criteria);
        return settlementBatchRepository.findAll(specification, page).map(settlementBatchMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SettlementBatchCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SettlementBatch> specification = createSpecification(criteria);
        return settlementBatchRepository.count(specification);
    }

    /**
     * Function to convert {@link SettlementBatchCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SettlementBatch> createSpecification(SettlementBatchCriteria criteria) {
        Specification<SettlementBatch> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(SettlementBatch_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), SettlementBatch_.id),
                    buildStringSpecification(criteria.getReference(), SettlementBatch_.reference),
                    buildSpecification(criteria.getStatus(), SettlementBatch_.status),
                    buildRangeSpecification(criteria.getTotalAmount(), SettlementBatch_.totalAmount),
                    buildStringSpecification(criteria.getCurrencyCode(), SettlementBatch_.currencyCode),
                    buildRangeSpecification(criteria.getScheduledAt(), SettlementBatch_.scheduledAt),
                    buildRangeSpecification(criteria.getCompletedAt(), SettlementBatch_.completedAt),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(SettlementBatch_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
