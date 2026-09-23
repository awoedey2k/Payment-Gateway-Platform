package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.JournalLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalLineDTO implements Serializable {

    private Long id;

    private BigDecimal debitAmount;

    private BigDecimal creditAmount;

    @NotNull
    private LedgerAccountDTO account;

    @NotNull
    private JournalEntryDTO journalEntry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public LedgerAccountDTO getAccount() {
        return account;
    }

    public void setAccount(LedgerAccountDTO account) {
        this.account = account;
    }

    public JournalEntryDTO getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(JournalEntryDTO journalEntry) {
        this.journalEntry = journalEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalLineDTO)) {
            return false;
        }

        JournalLineDTO journalLineDTO = (JournalLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, journalLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalLineDTO{" +
            "id=" + getId() +
            ", debitAmount=" + getDebitAmount() +
            ", creditAmount=" + getCreditAmount() +
            ", account=" + getAccount() +
            ", journalEntry=" + getJournalEntry() +
            "}";
    }
}
