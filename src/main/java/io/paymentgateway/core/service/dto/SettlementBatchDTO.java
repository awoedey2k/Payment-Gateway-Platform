package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.SettlementBatchStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.SettlementBatch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SettlementBatchDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private SettlementBatchStatus status;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private String currencyCode;

    @NotNull
    private Instant scheduledAt;

    private Instant completedAt;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public SettlementBatchStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementBatchStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
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
        if (!(o instanceof SettlementBatchDTO)) {
            return false;
        }

        SettlementBatchDTO settlementBatchDTO = (SettlementBatchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, settlementBatchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettlementBatchDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", scheduledAt='" + getScheduledAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
