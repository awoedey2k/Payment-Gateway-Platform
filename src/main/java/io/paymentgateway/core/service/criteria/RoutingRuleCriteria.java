package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.RoutingRuleScope;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.RoutingRule} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.RoutingRuleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /routing-rules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoutingRuleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RoutingRuleScope
     */
    public static class RoutingRuleScopeFilter extends Filter<RoutingRuleScope> {

        public RoutingRuleScopeFilter() {}

        public RoutingRuleScopeFilter(RoutingRuleScopeFilter filter) {
            super(filter);
        }

        @Override
        public RoutingRuleScopeFilter copy() {
            return new RoutingRuleScopeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter priority;

    private RoutingRuleScopeFilter scope;

    private StringFilter countryCode;

    private StringFilter currencyCode;

    private StringFilter cardBrand;

    private StringFilter primaryAdapter;

    private StringFilter fallbackAdapter;

    private IntegerFilter maxRetries;

    private BooleanFilter isActive;

    private LongFilter tenantId;

    private Boolean distinct;

    public RoutingRuleCriteria() {}

    public RoutingRuleCriteria(RoutingRuleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.priority = other.optionalPriority().map(IntegerFilter::copy).orElse(null);
        this.scope = other.optionalScope().map(RoutingRuleScopeFilter::copy).orElse(null);
        this.countryCode = other.optionalCountryCode().map(StringFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.cardBrand = other.optionalCardBrand().map(StringFilter::copy).orElse(null);
        this.primaryAdapter = other.optionalPrimaryAdapter().map(StringFilter::copy).orElse(null);
        this.fallbackAdapter = other.optionalFallbackAdapter().map(StringFilter::copy).orElse(null);
        this.maxRetries = other.optionalMaxRetries().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RoutingRuleCriteria copy() {
        return new RoutingRuleCriteria(this);
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

    public IntegerFilter getPriority() {
        return priority;
    }

    public Optional<IntegerFilter> optionalPriority() {
        return Optional.ofNullable(priority);
    }

    public IntegerFilter priority() {
        if (priority == null) {
            setPriority(new IntegerFilter());
        }
        return priority;
    }

    public void setPriority(IntegerFilter priority) {
        this.priority = priority;
    }

    public RoutingRuleScopeFilter getScope() {
        return scope;
    }

    public Optional<RoutingRuleScopeFilter> optionalScope() {
        return Optional.ofNullable(scope);
    }

    public RoutingRuleScopeFilter scope() {
        if (scope == null) {
            setScope(new RoutingRuleScopeFilter());
        }
        return scope;
    }

    public void setScope(RoutingRuleScopeFilter scope) {
        this.scope = scope;
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

    public StringFilter getCardBrand() {
        return cardBrand;
    }

    public Optional<StringFilter> optionalCardBrand() {
        return Optional.ofNullable(cardBrand);
    }

    public StringFilter cardBrand() {
        if (cardBrand == null) {
            setCardBrand(new StringFilter());
        }
        return cardBrand;
    }

    public void setCardBrand(StringFilter cardBrand) {
        this.cardBrand = cardBrand;
    }

    public StringFilter getPrimaryAdapter() {
        return primaryAdapter;
    }

    public Optional<StringFilter> optionalPrimaryAdapter() {
        return Optional.ofNullable(primaryAdapter);
    }

    public StringFilter primaryAdapter() {
        if (primaryAdapter == null) {
            setPrimaryAdapter(new StringFilter());
        }
        return primaryAdapter;
    }

    public void setPrimaryAdapter(StringFilter primaryAdapter) {
        this.primaryAdapter = primaryAdapter;
    }

    public StringFilter getFallbackAdapter() {
        return fallbackAdapter;
    }

    public Optional<StringFilter> optionalFallbackAdapter() {
        return Optional.ofNullable(fallbackAdapter);
    }

    public StringFilter fallbackAdapter() {
        if (fallbackAdapter == null) {
            setFallbackAdapter(new StringFilter());
        }
        return fallbackAdapter;
    }

    public void setFallbackAdapter(StringFilter fallbackAdapter) {
        this.fallbackAdapter = fallbackAdapter;
    }

    public IntegerFilter getMaxRetries() {
        return maxRetries;
    }

    public Optional<IntegerFilter> optionalMaxRetries() {
        return Optional.ofNullable(maxRetries);
    }

    public IntegerFilter maxRetries() {
        if (maxRetries == null) {
            setMaxRetries(new IntegerFilter());
        }
        return maxRetries;
    }

    public void setMaxRetries(IntegerFilter maxRetries) {
        this.maxRetries = maxRetries;
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
        final RoutingRuleCriteria that = (RoutingRuleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(scope, that.scope) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(cardBrand, that.cardBrand) &&
            Objects.equals(primaryAdapter, that.primaryAdapter) &&
            Objects.equals(fallbackAdapter, that.fallbackAdapter) &&
            Objects.equals(maxRetries, that.maxRetries) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            priority,
            scope,
            countryCode,
            currencyCode,
            cardBrand,
            primaryAdapter,
            fallbackAdapter,
            maxRetries,
            isActive,
            tenantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoutingRuleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPriority().map(f -> "priority=" + f + ", ").orElse("") +
            optionalScope().map(f -> "scope=" + f + ", ").orElse("") +
            optionalCountryCode().map(f -> "countryCode=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalCardBrand().map(f -> "cardBrand=" + f + ", ").orElse("") +
            optionalPrimaryAdapter().map(f -> "primaryAdapter=" + f + ", ").orElse("") +
            optionalFallbackAdapter().map(f -> "fallbackAdapter=" + f + ", ").orElse("") +
            optionalMaxRetries().map(f -> "maxRetries=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
