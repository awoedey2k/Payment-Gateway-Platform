package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.TransactionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.Transaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    private String tenantReference;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal feeAmount;

    @NotNull
    private BigDecimal netAmount;

    @NotNull
    private String currencyCode;

    @NotNull
    private String countryCode;

    @NotNull
    private String paymentMethodCode;

    @NotNull
    private String idempotencyKey;

    private String customerEmail;

    private String customerPhone;

    @NotNull
    private Instant createdAt;

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

    public String getTenantReference() {
        return tenantReference;
    }

    public void setTenantReference(String tenantReference) {
        this.tenantReference = tenantReference;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", tenantReference='" + getTenantReference() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", feeAmount=" + getFeeAmount() +
            ", netAmount=" + getNetAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", paymentMethodCode='" + getPaymentMethodCode() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", customerEmail='" + getCustomerEmail() + "'" +
            ", customerPhone='" + getCustomerPhone() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
