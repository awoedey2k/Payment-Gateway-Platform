package io.paymentgateway.core.domain;

import io.paymentgateway.core.domain.enumeration.LedgerAccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LedgerAccount.
 */
@Entity
@Table(name = "ledger_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LedgerAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "account_code", nullable = false, unique = true)
    private String accountCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private LedgerAccountType accountType;

    @NotNull
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LedgerAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountCode() {
        return this.accountCode;
    }

    public LedgerAccount accountCode(String accountCode) {
        this.setAccountCode(accountCode);
        return this;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public LedgerAccountType getAccountType() {
        return this.accountType;
    }

    public LedgerAccount accountType(LedgerAccountType accountType) {
        this.setAccountType(accountType);
        return this;
    }

    public void setAccountType(LedgerAccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public LedgerAccount currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LedgerAccount)) {
            return false;
        }
        return getId() != null && getId().equals(((LedgerAccount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LedgerAccount{" +
            "id=" + getId() +
            ", accountCode='" + getAccountCode() + "'" +
            ", accountType='" + getAccountType() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            "}";
    }
}
