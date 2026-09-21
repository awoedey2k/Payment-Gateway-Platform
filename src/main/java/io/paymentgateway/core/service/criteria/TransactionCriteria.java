package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.TransactionStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.Transaction} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionStatus
     */
    public static class TransactionStatusFilter extends Filter<TransactionStatus> {

        public TransactionStatusFilter() {}

        public TransactionStatusFilter(TransactionStatusFilter filter) {
            super(filter);
        }

        @Override
        public TransactionStatusFilter copy() {
            return new TransactionStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter tenantReference;

    private TransactionStatusFilter status;

    private BigDecimalFilter amount;

    private BigDecimalFilter feeAmount;

    private BigDecimalFilter netAmount;

    private StringFilter currencyCode;

    private StringFilter countryCode;

    private StringFilter paymentMethodCode;

    private StringFilter idempotencyKey;

    private StringFilter customerEmail;

    private StringFilter customerPhone;

    private InstantFilter createdAt;

    private InstantFilter completedAt;

    private LongFilter refundId;

    private LongFilter tenantId;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.tenantReference = other.optionalTenantReference().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TransactionStatusFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.feeAmount = other.optionalFeeAmount().map(BigDecimalFilter::copy).orElse(null);
        this.netAmount = other.optionalNetAmount().map(BigDecimalFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.countryCode = other.optionalCountryCode().map(StringFilter::copy).orElse(null);
        this.paymentMethodCode = other.optionalPaymentMethodCode().map(StringFilter::copy).orElse(null);
        this.idempotencyKey = other.optionalIdempotencyKey().map(StringFilter::copy).orElse(null);
        this.customerEmail = other.optionalCustomerEmail().map(StringFilter::copy).orElse(null);
        this.customerPhone = other.optionalCustomerPhone().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.refundId = other.optionalRefundId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getTenantReference() {
        return tenantReference;
    }

    public Optional<StringFilter> optionalTenantReference() {
        return Optional.ofNullable(tenantReference);
    }

    public StringFilter tenantReference() {
        if (tenantReference == null) {
            setTenantReference(new StringFilter());
        }
        return tenantReference;
    }

    public void setTenantReference(StringFilter tenantReference) {
        this.tenantReference = tenantReference;
    }

    public TransactionStatusFilter getStatus() {
        return status;
    }

    public Optional<TransactionStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TransactionStatusFilter status() {
        if (status == null) {
            setStatus(new TransactionStatusFilter());
        }
        return status;
    }

    public void setStatus(TransactionStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public BigDecimalFilter getFeeAmount() {
        return feeAmount;
    }

    public Optional<BigDecimalFilter> optionalFeeAmount() {
        return Optional.ofNullable(feeAmount);
    }

    public BigDecimalFilter feeAmount() {
        if (feeAmount == null) {
            setFeeAmount(new BigDecimalFilter());
        }
        return feeAmount;
    }

    public void setFeeAmount(BigDecimalFilter feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimalFilter getNetAmount() {
        return netAmount;
    }

    public Optional<BigDecimalFilter> optionalNetAmount() {
        return Optional.ofNullable(netAmount);
    }

    public BigDecimalFilter netAmount() {
        if (netAmount == null) {
            setNetAmount(new BigDecimalFilter());
        }
        return netAmount;
    }

    public void setNetAmount(BigDecimalFilter netAmount) {
        this.netAmount = netAmount;
    }

    public StringFilter getCurrencyCode() {
        return currencyCode;
    }

    public Optional<StringFilter> optionalCurrencyCode() {
        return Optional.ofNullable(currencyCode);
    }

    public StringFilter currencyCode() {
        if (currencyCode == null) {
            setCurrencyCode(new StringFilter());
        }
        return currencyCode;
    }

    public void setCurrencyCode(StringFilter currencyCode) {
        this.currencyCode = currencyCode;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public Optional<StringFilter> optionalCountryCode() {
        return Optional.ofNullable(countryCode);
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            setCountryCode(new StringFilter());
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public Optional<StringFilter> optionalPaymentMethodCode() {
        return Optional.ofNullable(paymentMethodCode);
    }

    public StringFilter paymentMethodCode() {
        if (paymentMethodCode == null) {
            setPaymentMethodCode(new StringFilter());
        }
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(StringFilter paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public StringFilter getIdempotencyKey() {
        return idempotencyKey;
    }

    public Optional<StringFilter> optionalIdempotencyKey() {
        return Optional.ofNullable(idempotencyKey);
    }

    public StringFilter idempotencyKey() {
        if (idempotencyKey == null) {
            setIdempotencyKey(new StringFilter());
        }
        return idempotencyKey;
    }

    public void setIdempotencyKey(StringFilter idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public StringFilter getCustomerEmail() {
        return customerEmail;
    }

    public Optional<StringFilter> optionalCustomerEmail() {
        return Optional.ofNullable(customerEmail);
    }

    public StringFilter customerEmail() {
        if (customerEmail == null) {
            setCustomerEmail(new StringFilter());
        }
        return customerEmail;
    }

    public void setCustomerEmail(StringFilter customerEmail) {
        this.customerEmail = customerEmail;
    }

    public StringFilter getCustomerPhone() {
        return customerPhone;
    }

    public Optional<StringFilter> optionalCustomerPhone() {
        return Optional.ofNullable(customerPhone);
    }

    public StringFilter customerPhone() {
        if (customerPhone == null) {
            setCustomerPhone(new StringFilter());
        }
        return customerPhone;
    }

    public void setCustomerPhone(StringFilter customerPhone) {
        this.customerPhone = customerPhone;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getCompletedAt() {
        return completedAt;
    }

    public Optional<InstantFilter> optionalCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InstantFilter completedAt() {
        if (completedAt == null) {
            setCompletedAt(new InstantFilter());
        }
        return completedAt;
    }

    public void setCompletedAt(InstantFilter completedAt) {
        this.completedAt = completedAt;
    }

    public LongFilter getRefundId() {
        return refundId;
    }

    public Optional<LongFilter> optionalRefundId() {
        return Optional.ofNullable(refundId);
    }

    public LongFilter refundId() {
        if (refundId == null) {
            setRefundId(new LongFilter());
        }
        return refundId;
    }

    public void setRefundId(LongFilter refundId) {
        this.refundId = refundId;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(tenantReference, that.tenantReference) &&
            Objects.equals(status, that.status) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(feeAmount, that.feeAmount) &&
            Objects.equals(netAmount, that.netAmount) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(paymentMethodCode, that.paymentMethodCode) &&
            Objects.equals(idempotencyKey, that.idempotencyKey) &&
            Objects.equals(customerEmail, that.customerEmail) &&
            Objects.equals(customerPhone, that.customerPhone) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(refundId, that.refundId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            tenantReference,
            status,
            amount,
            feeAmount,
            netAmount,
            currencyCode,
            countryCode,
            paymentMethodCode,
            idempotencyKey,
            customerEmail,
            customerPhone,
            createdAt,
            completedAt,
            refundId,
            tenantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTenantReference().map(f -> "tenantReference=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalFeeAmount().map(f -> "feeAmount=" + f + ", ").orElse("") +
            optionalNetAmount().map(f -> "netAmount=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalCountryCode().map(f -> "countryCode=" + f + ", ").orElse("") +
            optionalPaymentMethodCode().map(f -> "paymentMethodCode=" + f + ", ").orElse("") +
            optionalIdempotencyKey().map(f -> "idempotencyKey=" + f + ", ").orElse("") +
            optionalCustomerEmail().map(f -> "customerEmail=" + f + ", ").orElse("") +
            optionalCustomerPhone().map(f -> "customerPhone=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalRefundId().map(f -> "refundId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
