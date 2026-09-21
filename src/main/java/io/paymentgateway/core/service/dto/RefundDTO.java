package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.RefundReason;
import io.paymentgateway.core.domain.enumeration.RefundStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.Refund} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private RefundReason reason;

    @NotNull
    private RefundStatus status;

    @NotNull
    private Instant createdAt;

    @NotNull
    private TransactionDTO transaction;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RefundReason getReason() {
        return reason;
    }

    public void setReason(RefundReason reason) {
        this.reason = reason;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
        if (!(o instanceof RefundDTO)) {
            return false;
        }

        RefundDTO refundDTO = (RefundDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, refundDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", amount=" + getAmount() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", transaction=" + getTransaction() +
            "}";
    }
}
