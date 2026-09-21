package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.AuditLogEntry;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuditLogEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, Long>, JpaSpecificationExecutor<AuditLogEntry> {}
