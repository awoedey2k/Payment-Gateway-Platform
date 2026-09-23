package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.PayoutFrequency;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.PayoutSchedule} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.PayoutScheduleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payout-schedules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutScheduleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PayoutFrequency
     */
    public static class PayoutFrequencyFilter extends Filter<PayoutFrequency> {

        public PayoutFrequencyFilter() {}

        public PayoutFrequencyFilter(PayoutFrequencyFilter filter) {
            super(filter);
        }

        @Override
        public PayoutFrequencyFilter copy() {
            return new PayoutFrequencyFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private PayoutFrequencyFilter frequencyMode;

    private BigDecimalFilter thresholdAmount;

    private BooleanFilter isActive;

    private LongFilter tenantId;

    private Boolean distinct;

    public PayoutScheduleCriteria() {}

    public PayoutScheduleCriteria(PayoutScheduleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.frequencyMode = other.optionalFrequencyMode().map(PayoutFrequencyFilter::copy).orElse(null);
        this.thresholdAmount = other.optionalThresholdAmount().map(BigDecimalFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PayoutScheduleCriteria copy() {
        return new PayoutScheduleCriteria(this);
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

    public PayoutFrequencyFilter getFrequencyMode() {
        return frequencyMode;
    }

    public Optional<PayoutFrequencyFilter> optionalFrequencyMode() {
        return Optional.ofNullable(frequencyMode);
    }

    public PayoutFrequencyFilter frequencyMode() {
        if (frequencyMode == null) {
            setFrequencyMode(new PayoutFrequencyFilter());
        }
        return frequencyMode;
    }

    public void setFrequencyMode(PayoutFrequencyFilter frequencyMode) {
        this.frequencyMode = frequencyMode;
    }

    public BigDecimalFilter getThresholdAmount() {
        return thresholdAmount;
    }

    public Optional<BigDecimalFilter> optionalThresholdAmount() {
        return Optional.ofNullable(thresholdAmount);
    }

    public BigDecimalFilter thresholdAmount() {
        if (thresholdAmount == null) {
            setThresholdAmount(new BigDecimalFilter());
        }
        return thresholdAmount;
    }

    public void setThresholdAmount(BigDecimalFilter thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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
        final PayoutScheduleCriteria that = (PayoutScheduleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(frequencyMode, that.frequencyMode) &&
            Objects.equals(thresholdAmount, that.thresholdAmount) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frequencyMode, thresholdAmount, isActive, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutScheduleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFrequencyMode().map(f -> "frequencyMode=" + f + ", ").orElse("") +
            optionalThresholdAmount().map(f -> "thresholdAmount=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
