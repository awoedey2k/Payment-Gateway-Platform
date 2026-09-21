package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.Currency;
import io.paymentgateway.core.repository.CurrencyRepository;
import io.paymentgateway.core.service.criteria.CurrencyCriteria;
import io.paymentgateway.core.service.dto.CurrencyDTO;
import io.paymentgateway.core.service.mapper.CurrencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Currency} entities in the database.
 * The main input is a {@link CurrencyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CurrencyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CurrencyQueryService extends QueryService<Currency> {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyQueryService.class);

    private final CurrencyRepository currencyRepository;

    private final CurrencyMapper currencyMapper;

    public CurrencyQueryService(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
    }

    /**
     * Return a {@link Page} of {@link CurrencyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CurrencyDTO> findByCriteria(CurrencyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Currency> specification = createSpecification(criteria);
        return currencyRepository.findAll(specification, page).map(currencyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CurrencyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Currency> specification = createSpecification(criteria);
        return currencyRepository.count(specification);
    }

    /**
     * Function to convert {@link CurrencyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Currency> createSpecification(CurrencyCriteria criteria) {
        Specification<Currency> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Currency_.id),
                    buildStringSpecification(criteria.getCode(), Currency_.code),
                    buildStringSpecification(criteria.getName(), Currency_.name),
                    buildRangeSpecification(criteria.getMinorUnit(), Currency_.minorUnit)
                )
            );
        }
        return specification;
    }
}
