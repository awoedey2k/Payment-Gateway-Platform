package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.RefundReason;
import io.paymentgateway.core.domain.enumeration.RefundStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.Refund} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.RefundResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /refunds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RefundReason
     */
    public static class RefundReasonFilter extends Filter<RefundReason> {

        public RefundReasonFilter() {}

        public RefundReasonFilter(RefundReasonFilter filter) {
            super(filter);
        }

        @Override
        public RefundReasonFilter copy() {
            return new RefundReasonFilter(this);
        }
    }

    /**
     * Class for filtering RefundStatus
     */
    public static class RefundStatusFilter extends Filter<RefundStatus> {

        public RefundStatusFilter() {}

        public RefundStatusFilter(RefundStatusFilter filter) {
            super(filter);
        }

        @Override
        public RefundStatusFilter copy() {
            return new RefundStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private BigDecimalFilter amount;

    private RefundReasonFilter reason;

    private RefundStatusFilter status;

    private InstantFilter createdAt;

    private LongFilter transactionId;

    private Boolean distinct;

    public RefundCriteria() {}

    public RefundCriteria(RefundCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(RefundReasonFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RefundStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RefundCriteria copy() {
        return new RefundCriteria(this);
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

    public RefundReasonFilter getReason() {
        return reason;
    }

    public Optional<RefundReasonFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public RefundReasonFilter reason() {
        if (reason == null) {
            setReason(new RefundReasonFilter());
        }
        return reason;
    }

    public void setReason(RefundReasonFilter reason) {
        this.reason = reason;
    }

    public RefundStatusFilter getStatus() {
        return status;
    }

    public Optional<RefundStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public RefundStatusFilter status() {
        if (status == null) {
            setStatus(new RefundStatusFilter());
        }
        return status;
    }

    public void setStatus(RefundStatusFilter status) {
        this.status = status;
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

    public LongFilter getTransactionId() {
        return transactionId;
    }

    public Optional<LongFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public LongFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new LongFilter());
        }
        return transactionId;
    }

    public void setTransactionId(LongFilter transactionId) {
        this.transactionId = transactionId;
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
        final RefundCriteria that = (RefundCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, amount, reason, status, createdAt, transactionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
