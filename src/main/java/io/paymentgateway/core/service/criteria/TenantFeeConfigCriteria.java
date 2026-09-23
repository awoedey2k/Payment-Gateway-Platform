package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.FeeBearer;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.TenantFeeConfig} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.TenantFeeConfigResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenant-fee-configs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantFeeConfigCriteria implements Serializable, Criteria {

    /**
     * Class for filtering FeeBearer
     */
    public static class FeeBearerFilter extends Filter<FeeBearer> {

        public FeeBearerFilter() {}

        public FeeBearerFilter(FeeBearerFilter filter) {
            super(filter);
        }

        @Override
        public FeeBearerFilter copy() {
            return new FeeBearerFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter fixedFee;

    private BigDecimalFilter percentageFee;

    private BigDecimalFilter capAmount;

    private FeeBearerFilter feeBearer;

    private BooleanFilter isActive;

    private LongFilter tenantId;

    private LongFilter countryPaymentMethodId;

    private Boolean distinct;

    public TenantFeeConfigCriteria() {}

    public TenantFeeConfigCriteria(TenantFeeConfigCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fixedFee = other.optionalFixedFee().map(BigDecimalFilter::copy).orElse(null);
        this.percentageFee = other.optionalPercentageFee().map(BigDecimalFilter::copy).orElse(null);
        this.capAmount = other.optionalCapAmount().map(BigDecimalFilter::copy).orElse(null);
        this.feeBearer = other.optionalFeeBearer().map(FeeBearerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.countryPaymentMethodId = other.optionalCountryPaymentMethodId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantFeeConfigCriteria copy() {
        return new TenantFeeConfigCriteria(this);
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

    public BigDecimalFilter getFixedFee() {
        return fixedFee;
    }

    public Optional<BigDecimalFilter> optionalFixedFee() {
        return Optional.ofNullable(fixedFee);
    }

    public BigDecimalFilter fixedFee() {
        if (fixedFee == null) {
            setFixedFee(new BigDecimalFilter());
        }
        return fixedFee;
    }

    public void setFixedFee(BigDecimalFilter fixedFee) {
        this.fixedFee = fixedFee;
    }

    public BigDecimalFilter getPercentageFee() {
        return percentageFee;
    }

    public Optional<BigDecimalFilter> optionalPercentageFee() {
        return Optional.ofNullable(percentageFee);
    }

    public BigDecimalFilter percentageFee() {
        if (percentageFee == null) {
            setPercentageFee(new BigDecimalFilter());
        }
        return percentageFee;
    }

    public void setPercentageFee(BigDecimalFilter percentageFee) {
        this.percentageFee = percentageFee;
    }

    public BigDecimalFilter getCapAmount() {
        return capAmount;
    }

    public Optional<BigDecimalFilter> optionalCapAmount() {
        return Optional.ofNullable(capAmount);
    }

    public BigDecimalFilter capAmount() {
        if (capAmount == null) {
            setCapAmount(new BigDecimalFilter());
        }
        return capAmount;
    }

    public void setCapAmount(BigDecimalFilter capAmount) {
        this.capAmount = capAmount;
    }

    public FeeBearerFilter getFeeBearer() {
        return feeBearer;
    }

    public Optional<FeeBearerFilter> optionalFeeBearer() {
        return Optional.ofNullable(feeBearer);
    }

    public FeeBearerFilter feeBearer() {
        if (feeBearer == null) {
            setFeeBearer(new FeeBearerFilter());
        }
        return feeBearer;
    }

    public void setFeeBearer(FeeBearerFilter feeBearer) {
        this.feeBearer = feeBearer;
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

    public LongFilter getCountryPaymentMethodId() {
        return countryPaymentMethodId;
    }

    public Optional<LongFilter> optionalCountryPaymentMethodId() {
        return Optional.ofNullable(countryPaymentMethodId);
    }

    public LongFilter countryPaymentMethodId() {
        if (countryPaymentMethodId == null) {
            setCountryPaymentMethodId(new LongFilter());
        }
        return countryPaymentMethodId;
    }

    public void setCountryPaymentMethodId(LongFilter countryPaymentMethodId) {
        this.countryPaymentMethodId = countryPaymentMethodId;
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
        final TenantFeeConfigCriteria that = (TenantFeeConfigCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fixedFee, that.fixedFee) &&
            Objects.equals(percentageFee, that.percentageFee) &&
            Objects.equals(capAmount, that.capAmount) &&
            Objects.equals(feeBearer, that.feeBearer) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(countryPaymentMethodId, that.countryPaymentMethodId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fixedFee, percentageFee, capAmount, feeBearer, isActive, tenantId, countryPaymentMethodId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantFeeConfigCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFixedFee().map(f -> "fixedFee=" + f + ", ").orElse("") +
            optionalPercentageFee().map(f -> "percentageFee=" + f + ", ").orElse("") +
            optionalCapAmount().map(f -> "capAmount=" + f + ", ").orElse("") +
            optionalFeeBearer().map(f -> "feeBearer=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalCountryPaymentMethodId().map(f -> "countryPaymentMethodId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
