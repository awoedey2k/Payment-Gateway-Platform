package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.ForexRate;
import io.paymentgateway.core.repository.ForexRateRepository;
import io.paymentgateway.core.service.criteria.ForexRateCriteria;
import io.paymentgateway.core.service.dto.ForexRateDTO;
import io.paymentgateway.core.service.mapper.ForexRateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ForexRate} entities in the database.
 * The main input is a {@link ForexRateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ForexRateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ForexRateQueryService extends QueryService<ForexRate> {

    private static final Logger LOG = LoggerFactory.getLogger(ForexRateQueryService.class);

    private final ForexRateRepository forexRateRepository;

    private final ForexRateMapper forexRateMapper;

    public ForexRateQueryService(ForexRateRepository forexRateRepository, ForexRateMapper forexRateMapper) {
        this.forexRateRepository = forexRateRepository;
        this.forexRateMapper = forexRateMapper;
    }

    /**
     * Return a {@link Page} of {@link ForexRateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ForexRateDTO> findByCriteria(ForexRateCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ForexRate> specification = createSpecification(criteria);
        return forexRateRepository.findAll(specification, page).map(forexRateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ForexRateCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ForexRate> specification = createSpecification(criteria);
        return forexRateRepository.count(specification);
    }

    /**
     * Function to convert {@link ForexRateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ForexRate> createSpecification(ForexRateCriteria criteria) {
        Specification<ForexRate> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ForexRate_.id),
                    buildStringSpecification(criteria.getBaseCurrency(), ForexRate_.baseCurrency),
                    buildStringSpecification(criteria.getQuoteCurrency(), ForexRate_.quoteCurrency),
                    buildRangeSpecification(criteria.getRate(), ForexRate_.rate),
                    buildRangeSpecification(criteria.getPlatformSpreadBps(), ForexRate_.platformSpreadBps),
                    buildRangeSpecification(criteria.getLockedAt(), ForexRate_.lockedAt),
                    buildRangeSpecification(criteria.getExpiresAt(), ForexRate_.expiresAt)
                )
            );
        }
        return specification;
    }
}
