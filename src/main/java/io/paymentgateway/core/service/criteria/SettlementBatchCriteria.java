package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.SettlementBatchStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.SettlementBatch} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.SettlementBatchResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /settlement-batches?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SettlementBatchCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SettlementBatchStatus
     */
    public static class SettlementBatchStatusFilter extends Filter<SettlementBatchStatus> {

        public SettlementBatchStatusFilter() {}

        public SettlementBatchStatusFilter(SettlementBatchStatusFilter filter) {
            super(filter);
        }

        @Override
        public SettlementBatchStatusFilter copy() {
            return new SettlementBatchStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private SettlementBatchStatusFilter status;

    private BigDecimalFilter totalAmount;

    private StringFilter currencyCode;

    private InstantFilter scheduledAt;

    private InstantFilter completedAt;

    private LongFilter tenantId;

    private Boolean distinct;

    public SettlementBatchCriteria() {}

    public SettlementBatchCriteria(SettlementBatchCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SettlementBatchStatusFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.scheduledAt = other.optionalScheduledAt().map(InstantFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SettlementBatchCriteria copy() {
        return new SettlementBatchCriteria(this);
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

    public SettlementBatchStatusFilter getStatus() {
        return status;
    }

    public Optional<SettlementBatchStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SettlementBatchStatusFilter status() {
        if (status == null) {
            setStatus(new SettlementBatchStatusFilter());
        }
        return status;
    }

    public void setStatus(SettlementBatchStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
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

    public InstantFilter getScheduledAt() {
        return scheduledAt;
    }

    public Optional<InstantFilter> optionalScheduledAt() {
        return Optional.ofNullable(scheduledAt);
    }

    public InstantFilter scheduledAt() {
        if (scheduledAt == null) {
            setScheduledAt(new InstantFilter());
        }
        return scheduledAt;
    }

    public void setScheduledAt(InstantFilter scheduledAt) {
        this.scheduledAt = scheduledAt;
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
        final SettlementBatchCriteria that = (SettlementBatchCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(status, that.status) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(scheduledAt, that.scheduledAt) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, status, totalAmount, currencyCode, scheduledAt, completedAt, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettlementBatchCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalScheduledAt().map(f -> "scheduledAt=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
