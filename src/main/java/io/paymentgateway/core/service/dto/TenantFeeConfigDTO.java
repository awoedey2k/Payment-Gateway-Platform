package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.FeeBearer;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.TenantFeeConfig} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantFeeConfigDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal fixedFee;

    @NotNull
    private BigDecimal percentageFee;

    private BigDecimal capAmount;

    @NotNull
    private FeeBearer feeBearer;

    @NotNull
    private Boolean isActive;

    @NotNull
    private CorporateTenantDTO tenant;

    @NotNull
    private CountryPaymentMethodDTO countryPaymentMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFixedFee() {
        return fixedFee;
    }

    public void setFixedFee(BigDecimal fixedFee) {
        this.fixedFee = fixedFee;
    }

    public BigDecimal getPercentageFee() {
        return percentageFee;
    }

    public void setPercentageFee(BigDecimal percentageFee) {
        this.percentageFee = percentageFee;
    }

    public BigDecimal getCapAmount() {
        return capAmount;
    }

    public void setCapAmount(BigDecimal capAmount) {
        this.capAmount = capAmount;
    }

    public FeeBearer getFeeBearer() {
        return feeBearer;
    }

    public void setFeeBearer(FeeBearer feeBearer) {
        this.feeBearer = feeBearer;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    public CountryPaymentMethodDTO getCountryPaymentMethod() {
        return countryPaymentMethod;
    }

    public void setCountryPaymentMethod(CountryPaymentMethodDTO countryPaymentMethod) {
        this.countryPaymentMethod = countryPaymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantFeeConfigDTO)) {
            return false;
        }

        TenantFeeConfigDTO tenantFeeConfigDTO = (TenantFeeConfigDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tenantFeeConfigDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantFeeConfigDTO{" +
            "id=" + getId() +
            ", fixedFee=" + getFixedFee() +
            ", percentageFee=" + getPercentageFee() +
            ", capAmount=" + getCapAmount() +
            ", feeBearer='" + getFeeBearer() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", tenant=" + getTenant() +
            ", countryPaymentMethod=" + getCountryPaymentMethod() +
            "}";
    }
}
