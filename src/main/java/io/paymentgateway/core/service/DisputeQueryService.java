package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.repository.DisputeRepository;
import io.paymentgateway.core.service.criteria.DisputeCriteria;
import io.paymentgateway.core.service.dto.DisputeDTO;
import io.paymentgateway.core.service.mapper.DisputeMapper;
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
 * Service for executing complex queries for {@link Dispute} entities in the database.
 * The main input is a {@link DisputeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DisputeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DisputeQueryService extends QueryService<Dispute> {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeQueryService.class);

    private final DisputeRepository disputeRepository;

    private final DisputeMapper disputeMapper;

    public DisputeQueryService(DisputeRepository disputeRepository, DisputeMapper disputeMapper) {
        this.disputeRepository = disputeRepository;
        this.disputeMapper = disputeMapper;
    }

    /**
     * Return a {@link Page} of {@link DisputeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DisputeDTO> findByCriteria(DisputeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Dispute> specification = createSpecification(criteria);
        return disputeRepository.findAll(specification, page).map(disputeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DisputeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Dispute> specification = createSpecification(criteria);
        return disputeRepository.count(specification);
    }

    /**
     * Function to convert {@link DisputeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Dispute> createSpecification(DisputeCriteria criteria) {
        Specification<Dispute> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Dispute_.tenant, JoinType.LEFT);
                root.fetch(Dispute_.transaction, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Dispute_.id),
                    buildStringSpecification(criteria.getCaseReference(), Dispute_.caseReference),
                    buildRangeSpecification(criteria.getAmount(), Dispute_.amount),
                    buildStringSpecification(criteria.getCurrencyCode(), Dispute_.currencyCode),
                    buildStringSpecification(criteria.getReasonCode(), Dispute_.reasonCode),
                    buildStringSpecification(criteria.getReasonDescription(), Dispute_.reasonDescription),
                    buildSpecification(criteria.getStatus(), Dispute_.status),
                    buildRangeSpecification(criteria.getDueDate(), Dispute_.dueDate),
                    buildRangeSpecification(criteria.getEvidenceSubmittedAt(), Dispute_.evidenceSubmittedAt),
                    buildRangeSpecification(criteria.getResolvedAt(), Dispute_.resolvedAt),
                    buildSpecification(criteria.getTenantId(), root -> root.join(Dispute_.tenant, JoinType.LEFT).get(CorporateTenant_.id)),
                    buildSpecification(criteria.getTransactionId(), root ->
                        root.join(Dispute_.transaction, JoinType.LEFT).get(Transaction_.id)
                    )
                )
            );
        }
        return specification;
    }
}
