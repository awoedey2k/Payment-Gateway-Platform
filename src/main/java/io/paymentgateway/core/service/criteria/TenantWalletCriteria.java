package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.TenantWallet} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.TenantWalletResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenant-wallets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantWalletCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter currencyCode;

    private BigDecimalFilter availableBalance;

    private BigDecimalFilter lockedBalance;

    private LongFilter tenantId;

    private Boolean distinct;

    public TenantWalletCriteria() {}

    public TenantWalletCriteria(TenantWalletCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.availableBalance = other.optionalAvailableBalance().map(BigDecimalFilter::copy).orElse(null);
        this.lockedBalance = other.optionalLockedBalance().map(BigDecimalFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantWalletCriteria copy() {
        return new TenantWalletCriteria(this);
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

    public BigDecimalFilter getAvailableBalance() {
        return availableBalance;
    }

    public Optional<BigDecimalFilter> optionalAvailableBalance() {
        return Optional.ofNullable(availableBalance);
    }

    public BigDecimalFilter availableBalance() {
        if (availableBalance == null) {
            setAvailableBalance(new BigDecimalFilter());
        }
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimalFilter availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimalFilter getLockedBalance() {
        return lockedBalance;
    }

    public Optional<BigDecimalFilter> optionalLockedBalance() {
        return Optional.ofNullable(lockedBalance);
    }

    public BigDecimalFilter lockedBalance() {
        if (lockedBalance == null) {
            setLockedBalance(new BigDecimalFilter());
        }
        return lockedBalance;
    }

    public void setLockedBalance(BigDecimalFilter lockedBalance) {
        this.lockedBalance = lockedBalance;
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
        final TenantWalletCriteria that = (TenantWalletCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(availableBalance, that.availableBalance) &&
            Objects.equals(lockedBalance, that.lockedBalance) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyCode, availableBalance, lockedBalance, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantWalletCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalAvailableBalance().map(f -> "availableBalance=" + f + ", ").orElse("") +
            optionalLockedBalance().map(f -> "lockedBalance=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
