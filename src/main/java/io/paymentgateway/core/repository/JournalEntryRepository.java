package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.JournalEntry;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the JournalEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>, JpaSpecificationExecutor<JournalEntry> {}
