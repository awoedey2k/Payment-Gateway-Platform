package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.LedgerAccountType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.LedgerAccount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LedgerAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String accountCode;

    @NotNull
    private LedgerAccountType accountType;

    @NotNull
    private String currencyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public LedgerAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(LedgerAccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LedgerAccountDTO)) {
            return false;
        }

        LedgerAccountDTO ledgerAccountDTO = (LedgerAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ledgerAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LedgerAccountDTO{" +
            "id=" + getId() +
            ", accountCode='" + getAccountCode() + "'" +
            ", accountType='" + getAccountType() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            "}";
    }
}
