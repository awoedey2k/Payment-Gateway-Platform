package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.JournalLine;
import io.paymentgateway.core.repository.JournalLineRepository;
import io.paymentgateway.core.service.criteria.JournalLineCriteria;
import io.paymentgateway.core.service.dto.JournalLineDTO;
import io.paymentgateway.core.service.mapper.JournalLineMapper;
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
 * Service for executing complex queries for {@link JournalLine} entities in the database.
 * The main input is a {@link JournalLineCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link JournalLineDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JournalLineQueryService extends QueryService<JournalLine> {

    private static final Logger LOG = LoggerFactory.getLogger(JournalLineQueryService.class);

    private final JournalLineRepository journalLineRepository;

    private final JournalLineMapper journalLineMapper;

    public JournalLineQueryService(JournalLineRepository journalLineRepository, JournalLineMapper journalLineMapper) {
        this.journalLineRepository = journalLineRepository;
        this.journalLineMapper = journalLineMapper;
    }

    /**
     * Return a {@link Page} of {@link JournalLineDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JournalLineDTO> findByCriteria(JournalLineCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JournalLine> specification = createSpecification(criteria);
        return journalLineRepository.findAll(specification, page).map(journalLineMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JournalLineCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<JournalLine> specification = createSpecification(criteria);
        return journalLineRepository.count(specification);
    }

    /**
     * Function to convert {@link JournalLineCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JournalLine> createSpecification(JournalLineCriteria criteria) {
        Specification<JournalLine> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(JournalLine_.account, JoinType.LEFT);
                root.fetch(JournalLine_.journalEntry, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), JournalLine_.id),
                    buildRangeSpecification(criteria.getDebitAmount(), JournalLine_.debitAmount),
                    buildRangeSpecification(criteria.getCreditAmount(), JournalLine_.creditAmount),
                    buildSpecification(criteria.getAccountId(), root ->
                        root.join(JournalLine_.account, JoinType.LEFT).get(LedgerAccount_.id)
                    ),
                    buildSpecification(criteria.getJournalEntryId(), root ->
                        root.join(JournalLine_.journalEntry, JoinType.LEFT).get(JournalEntry_.id)
                    )
                )
            );
        }
        return specification;
    }
}
