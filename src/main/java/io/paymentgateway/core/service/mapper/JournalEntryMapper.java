package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link JournalEntry} and its DTO {@link JournalEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface JournalEntryMapper extends EntityMapper<JournalEntryDTO, JournalEntry> {}
