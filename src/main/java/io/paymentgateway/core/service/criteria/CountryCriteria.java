package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.Country} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.CountryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /countries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CountryCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isoCode;

    private StringFilter name;

    private StringFilter defaultCurrencyCode;

    private Boolean distinct;

    public CountryCriteria() {}

    public CountryCriteria(CountryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isoCode = other.optionalIsoCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.defaultCurrencyCode = other.optionalDefaultCurrencyCode().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CountryCriteria copy() {
        return new CountryCriteria(this);
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

    public StringFilter getIsoCode() {
        return isoCode;
    }

    public Optional<StringFilter> optionalIsoCode() {
        return Optional.ofNullable(isoCode);
    }

    public StringFilter isoCode() {
        if (isoCode == null) {
            setIsoCode(new StringFilter());
        }
        return isoCode;
    }

    public void setIsoCode(StringFilter isoCode) {
        this.isoCode = isoCode;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDefaultCurrencyCode() {
        return defaultCurrencyCode;
    }

    public Optional<StringFilter> optionalDefaultCurrencyCode() {
        return Optional.ofNullable(defaultCurrencyCode);
    }

    public StringFilter defaultCurrencyCode() {
        if (defaultCurrencyCode == null) {
            setDefaultCurrencyCode(new StringFilter());
        }
        return defaultCurrencyCode;
    }

    public void setDefaultCurrencyCode(StringFilter defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
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
        final CountryCriteria that = (CountryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isoCode, that.isoCode) &&
            Objects.equals(name, that.name) &&
            Objects.equals(defaultCurrencyCode, that.defaultCurrencyCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isoCode, name, defaultCurrencyCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsoCode().map(f -> "isoCode=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDefaultCurrencyCode().map(f -> "defaultCurrencyCode=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
