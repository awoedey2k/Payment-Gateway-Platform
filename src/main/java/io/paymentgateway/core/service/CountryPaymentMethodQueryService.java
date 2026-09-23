package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.repository.CountryPaymentMethodRepository;
import io.paymentgateway.core.service.criteria.CountryPaymentMethodCriteria;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import io.paymentgateway.core.service.mapper.CountryPaymentMethodMapper;
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
 * Service for executing complex queries for {@link CountryPaymentMethod} entities in the database.
 * The main input is a {@link CountryPaymentMethodCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CountryPaymentMethodDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CountryPaymentMethodQueryService extends QueryService<CountryPaymentMethod> {

    private static final Logger LOG = LoggerFactory.getLogger(CountryPaymentMethodQueryService.class);

    private final CountryPaymentMethodRepository countryPaymentMethodRepository;

    private final CountryPaymentMethodMapper countryPaymentMethodMapper;

    public CountryPaymentMethodQueryService(
        CountryPaymentMethodRepository countryPaymentMethodRepository,
        CountryPaymentMethodMapper countryPaymentMethodMapper
    ) {
        this.countryPaymentMethodRepository = countryPaymentMethodRepository;
        this.countryPaymentMethodMapper = countryPaymentMethodMapper;
    }

    /**
     * Return a {@link Page} of {@link CountryPaymentMethodDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CountryPaymentMethodDTO> findByCriteria(CountryPaymentMethodCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CountryPaymentMethod> specification = createSpecification(criteria);
        return countryPaymentMethodRepository.findAll(specification, page).map(countryPaymentMethodMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CountryPaymentMethodCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CountryPaymentMethod> specification = createSpecification(criteria);
        return countryPaymentMethodRepository.count(specification);
    }

    /**
     * Function to convert {@link CountryPaymentMethodCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CountryPaymentMethod> createSpecification(CountryPaymentMethodCriteria criteria) {
        Specification<CountryPaymentMethod> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CountryPaymentMethod_.country, JoinType.LEFT);
                root.fetch(CountryPaymentMethod_.paymentMethod, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CountryPaymentMethod_.id),
                    buildRangeSpecification(criteria.getMinTxnAmount(), CountryPaymentMethod_.minTxnAmount),
                    buildRangeSpecification(criteria.getMaxTxnAmount(), CountryPaymentMethod_.maxTxnAmount),
                    buildSpecification(criteria.getSupportsRecurring(), CountryPaymentMethod_.supportsRecurring),
                    buildSpecification(criteria.getSupportsInstantRefund(), CountryPaymentMethod_.supportsInstantRefund),
                    buildSpecification(criteria.getIsActive(), CountryPaymentMethod_.isActive),
                    buildSpecification(criteria.getCountryId(), root ->
                        root.join(CountryPaymentMethod_.country, JoinType.LEFT).get(Country_.id)
                    ),
                    buildSpecification(criteria.getPaymentMethodId(), root ->
                        root.join(CountryPaymentMethod_.paymentMethod, JoinType.LEFT).get(PaymentMethod_.id)
                    )
                )
            );
        }
        return specification;
    }
}
