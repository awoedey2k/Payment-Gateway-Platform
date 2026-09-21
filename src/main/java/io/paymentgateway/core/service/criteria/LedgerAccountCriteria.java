package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.LedgerAccountType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.LedgerAccount} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.LedgerAccountResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ledger-accounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LedgerAccountCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LedgerAccountType
     */
    public static class LedgerAccountTypeFilter extends Filter<LedgerAccountType> {

        public LedgerAccountTypeFilter() {}

        public LedgerAccountTypeFilter(LedgerAccountTypeFilter filter) {
            super(filter);
        }

        @Override
        public LedgerAccountTypeFilter copy() {
            return new LedgerAccountTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter accountCode;

    private LedgerAccountTypeFilter accountType;

    private StringFilter currencyCode;

    private Boolean distinct;

    public LedgerAccountCriteria() {}

    public LedgerAccountCriteria(LedgerAccountCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.accountCode = other.optionalAccountCode().map(StringFilter::copy).orElse(null);
        this.accountType = other.optionalAccountType().map(LedgerAccountTypeFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LedgerAccountCriteria copy() {
        return new LedgerAccountCriteria(this);
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

    public StringFilter getAccountCode() {
        return accountCode;
    }

    public Optional<StringFilter> optionalAccountCode() {
        return Optional.ofNullable(accountCode);
    }

    public StringFilter accountCode() {
        if (accountCode == null) {
            setAccountCode(new StringFilter());
        }
        return accountCode;
    }

    public void setAccountCode(StringFilter accountCode) {
        this.accountCode = accountCode;
    }

    public LedgerAccountTypeFilter getAccountType() {
        return accountType;
    }

    public Optional<LedgerAccountTypeFilter> optionalAccountType() {
        return Optional.ofNullable(accountType);
    }

    public LedgerAccountTypeFilter accountType() {
        if (accountType == null) {
            setAccountType(new LedgerAccountTypeFilter());
        }
        return accountType;
    }

    public void setAccountType(LedgerAccountTypeFilter accountType) {
        this.accountType = accountType;
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
        final LedgerAccountCriteria that = (LedgerAccountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(accountCode, that.accountCode) &&
            Objects.equals(accountType, that.accountType) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountCode, accountType, currencyCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LedgerAccountCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAccountCode().map(f -> "accountCode=" + f + ", ").orElse("") +
            optionalAccountType().map(f -> "accountType=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
