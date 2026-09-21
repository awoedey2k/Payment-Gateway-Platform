package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.Country;
import io.paymentgateway.core.repository.CountryRepository;
import io.paymentgateway.core.service.criteria.CountryCriteria;
import io.paymentgateway.core.service.dto.CountryDTO;
import io.paymentgateway.core.service.mapper.CountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Country} entities in the database.
 * The main input is a {@link CountryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CountryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CountryQueryService extends QueryService<Country> {

    private static final Logger LOG = LoggerFactory.getLogger(CountryQueryService.class);

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    public CountryQueryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    /**
     * Return a {@link Page} of {@link CountryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CountryDTO> findByCriteria(CountryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Country> specification = createSpecification(criteria);
        return countryRepository.findAll(specification, page).map(countryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CountryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Country> specification = createSpecification(criteria);
        return countryRepository.count(specification);
    }

    /**
     * Function to convert {@link CountryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Country> createSpecification(CountryCriteria criteria) {
        Specification<Country> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Country_.id),
                    buildStringSpecification(criteria.getIsoCode(), Country_.isoCode),
                    buildStringSpecification(criteria.getName(), Country_.name),
                    buildStringSpecification(criteria.getDefaultCurrencyCode(), Country_.defaultCurrencyCode)
                )
            );
        }
        return specification;
    }
}
