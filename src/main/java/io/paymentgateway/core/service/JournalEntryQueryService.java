package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.repository.JournalEntryRepository;
import io.paymentgateway.core.service.criteria.JournalEntryCriteria;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
import io.paymentgateway.core.service.mapper.JournalEntryMapper;
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
 * Service for executing complex queries for {@link JournalEntry} entities in the database.
 * The main input is a {@link JournalEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link JournalEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JournalEntryQueryService extends QueryService<JournalEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(JournalEntryQueryService.class);

    private final JournalEntryRepository journalEntryRepository;

    private final JournalEntryMapper journalEntryMapper;

    public JournalEntryQueryService(JournalEntryRepository journalEntryRepository, JournalEntryMapper journalEntryMapper) {
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryMapper = journalEntryMapper;
    }

    /**
     * Return a {@link Page} of {@link JournalEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JournalEntryDTO> findByCriteria(JournalEntryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JournalEntry> specification = createSpecification(criteria);
        return journalEntryRepository.findAll(specification, page).map(journalEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JournalEntryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<JournalEntry> specification = createSpecification(criteria);
        return journalEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link JournalEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JournalEntry> createSpecification(JournalEntryCriteria criteria) {
        Specification<JournalEntry> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), JournalEntry_.id),
                    buildStringSpecification(criteria.getReference(), JournalEntry_.reference),
                    buildStringSpecification(criteria.getDescription(), JournalEntry_.description),
                    buildRangeSpecification(criteria.getPostedAt(), JournalEntry_.postedAt),
                    buildSpecification(criteria.getJournalLineId(), root ->
                        root.join(JournalEntry_.journalLines, JoinType.LEFT).get(JournalLine_.id)
                    )
                )
            );
        }
        return specification;
    }
}
