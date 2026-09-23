package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.DisputeStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.Dispute} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeDTO implements Serializable {

    private Long id;

    @NotNull
    private String caseReference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currencyCode;

    @NotNull
    private String reasonCode;

    private String reasonDescription;

    @NotNull
    private DisputeStatus status;

    @NotNull
    private Instant dueDate;

    private Instant evidenceSubmittedAt;

    private Instant resolvedAt;

    @NotNull
    private CorporateTenantDTO tenant;

    @NotNull
    private TransactionDTO transaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseReference() {
        return caseReference;
    }

    public void setCaseReference(String caseReference) {
        this.caseReference = caseReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getEvidenceSubmittedAt() {
        return evidenceSubmittedAt;
    }

    public void setEvidenceSubmittedAt(Instant evidenceSubmittedAt) {
        this.evidenceSubmittedAt = evidenceSubmittedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisputeDTO)) {
            return false;
        }

        DisputeDTO disputeDTO = (DisputeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, disputeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeDTO{" +
            "id=" + getId() +
            ", caseReference='" + getCaseReference() + "'" +
            ", amount=" + getAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", reasonCode='" + getReasonCode() + "'" +
            ", reasonDescription='" + getReasonDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", evidenceSubmittedAt='" + getEvidenceSubmittedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", tenant=" + getTenant() +
            ", transaction=" + getTransaction() +
            "}";
    }
}
