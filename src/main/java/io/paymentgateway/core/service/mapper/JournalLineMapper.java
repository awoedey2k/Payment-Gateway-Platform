package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.domain.JournalLine;
import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
import io.paymentgateway.core.service.dto.JournalLineDTO;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link JournalLine} and its DTO {@link JournalLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface JournalLineMapper extends EntityMapper<JournalLineDTO, JournalLine> {
    @Mapping(target = "account", source = "account", qualifiedByName = "ledgerAccountId")
    @Mapping(target = "journalEntry", source = "journalEntry", qualifiedByName = "journalEntryId")
    JournalLineDTO toDto(JournalLine s);

    @Named("ledgerAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LedgerAccountDTO toDtoLedgerAccountId(LedgerAccount ledgerAccount);

    @Named("journalEntryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    JournalEntryDTO toDtoJournalEntryId(JournalEntry journalEntry);
}
