package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.repository.LedgerAccountRepository;
import io.paymentgateway.core.service.criteria.LedgerAccountCriteria;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import io.paymentgateway.core.service.mapper.LedgerAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LedgerAccount} entities in the database.
 * The main input is a {@link LedgerAccountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LedgerAccountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LedgerAccountQueryService extends QueryService<LedgerAccount> {

    private static final Logger LOG = LoggerFactory.getLogger(LedgerAccountQueryService.class);

    private final LedgerAccountRepository ledgerAccountRepository;

    private final LedgerAccountMapper ledgerAccountMapper;

    public LedgerAccountQueryService(LedgerAccountRepository ledgerAccountRepository, LedgerAccountMapper ledgerAccountMapper) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.ledgerAccountMapper = ledgerAccountMapper;
    }

    /**
     * Return a {@link Page} of {@link LedgerAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LedgerAccountDTO> findByCriteria(LedgerAccountCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LedgerAccount> specification = createSpecification(criteria);
        return ledgerAccountRepository.findAll(specification, page).map(ledgerAccountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LedgerAccountCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LedgerAccount> specification = createSpecification(criteria);
        return ledgerAccountRepository.count(specification);
    }

    /**
     * Function to convert {@link LedgerAccountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LedgerAccount> createSpecification(LedgerAccountCriteria criteria) {
        Specification<LedgerAccount> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), LedgerAccount_.id),
                    buildStringSpecification(criteria.getAccountCode(), LedgerAccount_.accountCode),
                    buildSpecification(criteria.getAccountType(), LedgerAccount_.accountType),
                    buildStringSpecification(criteria.getCurrencyCode(), LedgerAccount_.currencyCode)
                )
            );
        }
        return specification;
    }
}
