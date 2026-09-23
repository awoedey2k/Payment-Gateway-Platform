package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A JournalLine.
 */
@Entity
@Table(name = "journal_line")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalLine implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "debit_amount", precision = 21, scale = 2)
    private BigDecimal debitAmount;

    @Column(name = "credit_amount", precision = 21, scale = 2)
    private BigDecimal creditAmount;

    @ManyToOne(optional = false)
    @NotNull
    private LedgerAccount account;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "journalLines" }, allowSetters = true)
    private JournalEntry journalEntry;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JournalLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDebitAmount() {
        return this.debitAmount;
    }

    public JournalLine debitAmount(BigDecimal debitAmount) {
        this.setDebitAmount(debitAmount);
        return this;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return this.creditAmount;
    }

    public JournalLine creditAmount(BigDecimal creditAmount) {
        this.setCreditAmount(creditAmount);
        return this;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public LedgerAccount getAccount() {
        return this.account;
    }

    public void setAccount(LedgerAccount ledgerAccount) {
        this.account = ledgerAccount;
    }

    public JournalLine account(LedgerAccount ledgerAccount) {
        this.setAccount(ledgerAccount);
        return this;
    }

    public JournalEntry getJournalEntry() {
        return this.journalEntry;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    public JournalLine journalEntry(JournalEntry journalEntry) {
        this.setJournalEntry(journalEntry);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalLine)) {
            return false;
        }
        return getId() != null && getId().equals(((JournalLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalLine{" +
            "id=" + getId() +
            ", debitAmount=" + getDebitAmount() +
            ", creditAmount=" + getCreditAmount() +
            "}";
    }
}
