package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.TenantWallet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantWalletDTO implements Serializable {

    private Long id;

    @NotNull
    private String currencyCode;

    @NotNull
    private BigDecimal availableBalance;

    @NotNull
    private BigDecimal lockedBalance;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(BigDecimal lockedBalance) {
        this.lockedBalance = lockedBalance;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantWalletDTO)) {
            return false;
        }

        TenantWalletDTO tenantWalletDTO = (TenantWalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tenantWalletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantWalletDTO{" +
            "id=" + getId() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", availableBalance=" + getAvailableBalance() +
            ", lockedBalance=" + getLockedBalance() +
            ", tenant=" + getTenant() +
            "}";
    }
}
