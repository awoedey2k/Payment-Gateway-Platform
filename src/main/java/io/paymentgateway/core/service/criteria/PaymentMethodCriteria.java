package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.PaymentMethodCategory;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.PaymentMethod} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.PaymentMethodResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payment-methods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethodCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PaymentMethodCategory
     */
    public static class PaymentMethodCategoryFilter extends Filter<PaymentMethodCategory> {

        public PaymentMethodCategoryFilter() {}

        public PaymentMethodCategoryFilter(PaymentMethodCategoryFilter filter) {
            super(filter);
        }

        @Override
        public PaymentMethodCategoryFilter copy() {
            return new PaymentMethodCategoryFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter displayName;

    private PaymentMethodCategoryFilter category;

    private Boolean distinct;

    public PaymentMethodCriteria() {}

    public PaymentMethodCriteria(PaymentMethodCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.displayName = other.optionalDisplayName().map(StringFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(PaymentMethodCategoryFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaymentMethodCriteria copy() {
        return new PaymentMethodCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getDisplayName() {
        return displayName;
    }

    public Optional<StringFilter> optionalDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public StringFilter displayName() {
        if (displayName == null) {
            setDisplayName(new StringFilter());
        }
        return displayName;
    }

    public void setDisplayName(StringFilter displayName) {
        this.displayName = displayName;
    }

    public PaymentMethodCategoryFilter getCategory() {
        return category;
    }

    public Optional<PaymentMethodCategoryFilter> optionalCategory() {
        return Optional.ofNullable(category);
    }

    public PaymentMethodCategoryFilter category() {
        if (category == null) {
            setCategory(new PaymentMethodCategoryFilter());
        }
        return category;
    }

    public void setCategory(PaymentMethodCategoryFilter category) {
        this.category = category;
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
        final PaymentMethodCriteria that = (PaymentMethodCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(category, that.category) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, displayName, category, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethodCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDisplayName().map(f -> "displayName=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
