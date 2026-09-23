package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.PayoutSchedule;
import io.paymentgateway.core.repository.PayoutScheduleRepository;
import io.paymentgateway.core.service.criteria.PayoutScheduleCriteria;
import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
import io.paymentgateway.core.service.mapper.PayoutScheduleMapper;
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
 * Service for executing complex queries for {@link PayoutSchedule} entities in the database.
 * The main input is a {@link PayoutScheduleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PayoutScheduleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PayoutScheduleQueryService extends QueryService<PayoutSchedule> {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutScheduleQueryService.class);

    private final PayoutScheduleRepository payoutScheduleRepository;

    private final PayoutScheduleMapper payoutScheduleMapper;

    public PayoutScheduleQueryService(PayoutScheduleRepository payoutScheduleRepository, PayoutScheduleMapper payoutScheduleMapper) {
        this.payoutScheduleRepository = payoutScheduleRepository;
        this.payoutScheduleMapper = payoutScheduleMapper;
    }

    /**
     * Return a {@link Page} of {@link PayoutScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PayoutScheduleDTO> findByCriteria(PayoutScheduleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PayoutSchedule> specification = createSpecification(criteria);
        return payoutScheduleRepository.findAll(specification, page).map(payoutScheduleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PayoutScheduleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PayoutSchedule> specification = createSpecification(criteria);
        return payoutScheduleRepository.count(specification);
    }

    /**
     * Function to convert {@link PayoutScheduleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PayoutSchedule> createSpecification(PayoutScheduleCriteria criteria) {
        Specification<PayoutSchedule> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(PayoutSchedule_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), PayoutSchedule_.id),
                    buildSpecification(criteria.getFrequencyMode(), PayoutSchedule_.frequencyMode),
                    buildRangeSpecification(criteria.getThresholdAmount(), PayoutSchedule_.thresholdAmount),
                    buildSpecification(criteria.getIsActive(), PayoutSchedule_.isActive),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(PayoutSchedule_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
