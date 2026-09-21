package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.PayoutFrequency;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.PayoutSchedule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutScheduleDTO implements Serializable {

    private Long id;

    @NotNull
    private PayoutFrequency frequencyMode;

    @NotNull
    private BigDecimal thresholdAmount;

    @NotNull
    private Boolean isActive;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayoutFrequency getFrequencyMode() {
        return frequencyMode;
    }

    public void setFrequencyMode(PayoutFrequency frequencyMode) {
        this.frequencyMode = frequencyMode;
    }

    public BigDecimal getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayoutScheduleDTO)) {
            return false;
        }

        PayoutScheduleDTO payoutScheduleDTO = (PayoutScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, payoutScheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutScheduleDTO{" +
            "id=" + getId() +
            ", frequencyMode='" + getFrequencyMode() + "'" +
            ", thresholdAmount=" + getThresholdAmount() +
            ", isActive='" + getIsActive() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
