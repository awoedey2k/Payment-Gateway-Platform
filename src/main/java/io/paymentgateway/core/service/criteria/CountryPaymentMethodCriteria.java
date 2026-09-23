package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.CountryPaymentMethod} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.CountryPaymentMethodResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /country-payment-methods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CountryPaymentMethodCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter minTxnAmount;

    private BigDecimalFilter maxTxnAmount;

    private BooleanFilter supportsRecurring;

    private BooleanFilter supportsInstantRefund;

    private BooleanFilter isActive;

    private LongFilter countryId;

    private LongFilter paymentMethodId;

    private Boolean distinct;

    public CountryPaymentMethodCriteria() {}

    public CountryPaymentMethodCriteria(CountryPaymentMethodCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.minTxnAmount = other.optionalMinTxnAmount().map(BigDecimalFilter::copy).orElse(null);
        this.maxTxnAmount = other.optionalMaxTxnAmount().map(BigDecimalFilter::copy).orElse(null);
        this.supportsRecurring = other.optionalSupportsRecurring().map(BooleanFilter::copy).orElse(null);
        this.supportsInstantRefund = other.optionalSupportsInstantRefund().map(BooleanFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.countryId = other.optionalCountryId().map(LongFilter::copy).orElse(null);
        this.paymentMethodId = other.optionalPaymentMethodId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CountryPaymentMethodCriteria copy() {
        return new CountryPaymentMethodCriteria(this);
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

    public BigDecimalFilter getMinTxnAmount() {
        return minTxnAmount;
    }

    public Optional<BigDecimalFilter> optionalMinTxnAmount() {
        return Optional.ofNullable(minTxnAmount);
    }

    public BigDecimalFilter minTxnAmount() {
        if (minTxnAmount == null) {
            setMinTxnAmount(new BigDecimalFilter());
        }
        return minTxnAmount;
    }

    public void setMinTxnAmount(BigDecimalFilter minTxnAmount) {
        this.minTxnAmount = minTxnAmount;
    }

    public BigDecimalFilter getMaxTxnAmount() {
        return maxTxnAmount;
    }

    public Optional<BigDecimalFilter> optionalMaxTxnAmount() {
        return Optional.ofNullable(maxTxnAmount);
    }

    public BigDecimalFilter maxTxnAmount() {
        if (maxTxnAmount == null) {
            setMaxTxnAmount(new BigDecimalFilter());
        }
        return maxTxnAmount;
    }

    public void setMaxTxnAmount(BigDecimalFilter maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    public BooleanFilter getSupportsRecurring() {
        return supportsRecurring;
    }

    public Optional<BooleanFilter> optionalSupportsRecurring() {
        return Optional.ofNullable(supportsRecurring);
    }

    public BooleanFilter supportsRecurring() {
        if (supportsRecurring == null) {
            setSupportsRecurring(new BooleanFilter());
        }
        return supportsRecurring;
    }

    public void setSupportsRecurring(BooleanFilter supportsRecurring) {
        this.supportsRecurring = supportsRecurring;
    }

    public BooleanFilter getSupportsInstantRefund() {
        return supportsInstantRefund;
    }

    public Optional<BooleanFilter> optionalSupportsInstantRefund() {
        return Optional.ofNullable(supportsInstantRefund);
    }

    public BooleanFilter supportsInstantRefund() {
        if (supportsInstantRefund == null) {
            setSupportsInstantRefund(new BooleanFilter());
        }
        return supportsInstantRefund;
    }

    public void setSupportsInstantRefund(BooleanFilter supportsInstantRefund) {
        this.supportsInstantRefund = supportsInstantRefund;
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

    public LongFilter getCountryId() {
        return countryId;
    }

    public Optional<LongFilter> optionalCountryId() {
        return Optional.ofNullable(countryId);
    }

    public LongFilter countryId() {
        if (countryId == null) {
            setCountryId(new LongFilter());
        }
        return countryId;
    }

    public void setCountryId(LongFilter countryId) {
        this.countryId = countryId;
    }

    public LongFilter getPaymentMethodId() {
        return paymentMethodId;
    }

    public Optional<LongFilter> optionalPaymentMethodId() {
        return Optional.ofNullable(paymentMethodId);
    }

    public LongFilter paymentMethodId() {
        if (paymentMethodId == null) {
            setPaymentMethodId(new LongFilter());
        }
        return paymentMethodId;
    }

    public void setPaymentMethodId(LongFilter paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
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
        final CountryPaymentMethodCriteria that = (CountryPaymentMethodCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(minTxnAmount, that.minTxnAmount) &&
            Objects.equals(maxTxnAmount, that.maxTxnAmount) &&
            Objects.equals(supportsRecurring, that.supportsRecurring) &&
            Objects.equals(supportsInstantRefund, that.supportsInstantRefund) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(countryId, that.countryId) &&
            Objects.equals(paymentMethodId, that.paymentMethodId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            minTxnAmount,
            maxTxnAmount,
            supportsRecurring,
            supportsInstantRefund,
            isActive,
            countryId,
            paymentMethodId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryPaymentMethodCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMinTxnAmount().map(f -> "minTxnAmount=" + f + ", ").orElse("") +
            optionalMaxTxnAmount().map(f -> "maxTxnAmount=" + f + ", ").orElse("") +
            optionalSupportsRecurring().map(f -> "supportsRecurring=" + f + ", ").orElse("") +
            optionalSupportsInstantRefund().map(f -> "supportsInstantRefund=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCountryId().map(f -> "countryId=" + f + ", ").orElse("") +
            optionalPaymentMethodId().map(f -> "paymentMethodId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
