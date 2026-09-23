package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.Refund;
import io.paymentgateway.core.repository.RefundRepository;
import io.paymentgateway.core.service.criteria.RefundCriteria;
import io.paymentgateway.core.service.dto.RefundDTO;
import io.paymentgateway.core.service.mapper.RefundMapper;
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
 * Service for executing complex queries for {@link Refund} entities in the database.
 * The main input is a {@link RefundCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RefundDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RefundQueryService extends QueryService<Refund> {

    private static final Logger LOG = LoggerFactory.getLogger(RefundQueryService.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    public RefundQueryService(RefundRepository refundRepository, RefundMapper refundMapper) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
    }

    /**
     * Return a {@link Page} of {@link RefundDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RefundDTO> findByCriteria(RefundCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundRepository.findAll(specification, page).map(refundMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RefundCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundRepository.count(specification);
    }

    /**
     * Function to convert {@link RefundCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Refund> createSpecification(RefundCriteria criteria) {
        Specification<Refund> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Refund_.transaction, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Refund_.id),
                    buildStringSpecification(criteria.getReference(), Refund_.reference),
                    buildRangeSpecification(criteria.getAmount(), Refund_.amount),
                    buildSpecification(criteria.getReason(), Refund_.reason),
                    buildSpecification(criteria.getStatus(), Refund_.status),
                    buildRangeSpecification(criteria.getCreatedAt(), Refund_.createdAt),
                    buildSpecification(criteria.getTransactionId(), root ->
                        root.join(Refund_.transaction, JoinType.LEFT).get(Transaction_.id)
                    )
                )
            );
        }
        return specification;
    }
}
