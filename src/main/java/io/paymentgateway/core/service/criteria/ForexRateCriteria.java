package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.ForexRate} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.ForexRateResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /forex-rates?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ForexRateCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter baseCurrency;

    private StringFilter quoteCurrency;

    private BigDecimalFilter rate;

    private IntegerFilter platformSpreadBps;

    private InstantFilter lockedAt;

    private InstantFilter expiresAt;

    private Boolean distinct;

    public ForexRateCriteria() {}

    public ForexRateCriteria(ForexRateCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.baseCurrency = other.optionalBaseCurrency().map(StringFilter::copy).orElse(null);
        this.quoteCurrency = other.optionalQuoteCurrency().map(StringFilter::copy).orElse(null);
        this.rate = other.optionalRate().map(BigDecimalFilter::copy).orElse(null);
        this.platformSpreadBps = other.optionalPlatformSpreadBps().map(IntegerFilter::copy).orElse(null);
        this.lockedAt = other.optionalLockedAt().map(InstantFilter::copy).orElse(null);
        this.expiresAt = other.optionalExpiresAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ForexRateCriteria copy() {
        return new ForexRateCriteria(this);
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

    public StringFilter getBaseCurrency() {
        return baseCurrency;
    }

    public Optional<StringFilter> optionalBaseCurrency() {
        return Optional.ofNullable(baseCurrency);
    }

    public StringFilter baseCurrency() {
        if (baseCurrency == null) {
            setBaseCurrency(new StringFilter());
        }
        return baseCurrency;
    }

    public void setBaseCurrency(StringFilter baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public StringFilter getQuoteCurrency() {
        return quoteCurrency;
    }

    public Optional<StringFilter> optionalQuoteCurrency() {
        return Optional.ofNullable(quoteCurrency);
    }

    public StringFilter quoteCurrency() {
        if (quoteCurrency == null) {
            setQuoteCurrency(new StringFilter());
        }
        return quoteCurrency;
    }

    public void setQuoteCurrency(StringFilter quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimalFilter getRate() {
        return rate;
    }

    public Optional<BigDecimalFilter> optionalRate() {
        return Optional.ofNullable(rate);
    }

    public BigDecimalFilter rate() {
        if (rate == null) {
            setRate(new BigDecimalFilter());
        }
        return rate;
    }

    public void setRate(BigDecimalFilter rate) {
        this.rate = rate;
    }

    public IntegerFilter getPlatformSpreadBps() {
        return platformSpreadBps;
    }

    public Optional<IntegerFilter> optionalPlatformSpreadBps() {
        return Optional.ofNullable(platformSpreadBps);
    }

    public IntegerFilter platformSpreadBps() {
        if (platformSpreadBps == null) {
            setPlatformSpreadBps(new IntegerFilter());
        }
        return platformSpreadBps;
    }

    public void setPlatformSpreadBps(IntegerFilter platformSpreadBps) {
        this.platformSpreadBps = platformSpreadBps;
    }

    public InstantFilter getLockedAt() {
        return lockedAt;
    }

    public Optional<InstantFilter> optionalLockedAt() {
        return Optional.ofNullable(lockedAt);
    }

    public InstantFilter lockedAt() {
        if (lockedAt == null) {
            setLockedAt(new InstantFilter());
        }
        return lockedAt;
    }

    public void setLockedAt(InstantFilter lockedAt) {
        this.lockedAt = lockedAt;
    }

    public InstantFilter getExpiresAt() {
        return expiresAt;
    }

    public Optional<InstantFilter> optionalExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public InstantFilter expiresAt() {
        if (expiresAt == null) {
            setExpiresAt(new InstantFilter());
        }
        return expiresAt;
    }

    public void setExpiresAt(InstantFilter expiresAt) {
        this.expiresAt = expiresAt;
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
        final ForexRateCriteria that = (ForexRateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(baseCurrency, that.baseCurrency) &&
            Objects.equals(quoteCurrency, that.quoteCurrency) &&
            Objects.equals(rate, that.rate) &&
            Objects.equals(platformSpreadBps, that.platformSpreadBps) &&
            Objects.equals(lockedAt, that.lockedAt) &&
            Objects.equals(expiresAt, that.expiresAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, quoteCurrency, rate, platformSpreadBps, lockedAt, expiresAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ForexRateCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBaseCurrency().map(f -> "baseCurrency=" + f + ", ").orElse("") +
            optionalQuoteCurrency().map(f -> "quoteCurrency=" + f + ", ").orElse("") +
            optionalRate().map(f -> "rate=" + f + ", ").orElse("") +
            optionalPlatformSpreadBps().map(f -> "platformSpreadBps=" + f + ", ").orElse("") +
            optionalLockedAt().map(f -> "lockedAt=" + f + ", ").orElse("") +
            optionalExpiresAt().map(f -> "expiresAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
