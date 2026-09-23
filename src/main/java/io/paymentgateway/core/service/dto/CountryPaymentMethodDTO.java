package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.CountryPaymentMethod} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CountryPaymentMethodDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal minTxnAmount;

    @NotNull
    private BigDecimal maxTxnAmount;

    @NotNull
    private Boolean supportsRecurring;

    @NotNull
    private Boolean supportsInstantRefund;

    @NotNull
    private Boolean isActive;

    @NotNull
    private CountryDTO country;

    @NotNull
    private PaymentMethodDTO paymentMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMinTxnAmount() {
        return minTxnAmount;
    }

    public void setMinTxnAmount(BigDecimal minTxnAmount) {
        this.minTxnAmount = minTxnAmount;
    }

    public BigDecimal getMaxTxnAmount() {
        return maxTxnAmount;
    }

    public void setMaxTxnAmount(BigDecimal maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    public Boolean getSupportsRecurring() {
        return supportsRecurring;
    }

    public void setSupportsRecurring(Boolean supportsRecurring) {
        this.supportsRecurring = supportsRecurring;
    }

    public Boolean getSupportsInstantRefund() {
        return supportsInstantRefund;
    }

    public void setSupportsInstantRefund(Boolean supportsInstantRefund) {
        this.supportsInstantRefund = supportsInstantRefund;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public PaymentMethodDTO getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodDTO paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryPaymentMethodDTO)) {
            return false;
        }

        CountryPaymentMethodDTO countryPaymentMethodDTO = (CountryPaymentMethodDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, countryPaymentMethodDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryPaymentMethodDTO{" +
            "id=" + getId() +
            ", minTxnAmount=" + getMinTxnAmount() +
            ", maxTxnAmount=" + getMaxTxnAmount() +
            ", supportsRecurring='" + getSupportsRecurring() + "'" +
            ", supportsInstantRefund='" + getSupportsInstantRefund() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", country=" + getCountry() +
            ", paymentMethod=" + getPaymentMethod() +
            "}";
    }
}
