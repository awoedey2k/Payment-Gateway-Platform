package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.AuditLogEntry;
import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLogEntry} and its DTO {@link AuditLogEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogEntryMapper extends EntityMapper<AuditLogEntryDTO, AuditLogEntry> {}
