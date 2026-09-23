package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.IdentificationType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.TenantDirector} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.TenantDirectorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenant-directors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDirectorCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CountryCode
     */
    public static class CountryCodeFilter extends Filter<CountryCode> {

        public CountryCodeFilter() {}

        public CountryCodeFilter(CountryCodeFilter filter) {
            super(filter);
        }

        @Override
        public CountryCodeFilter copy() {
            return new CountryCodeFilter(this);
        }
    }

    /**
     * Class for filtering IdentificationType
     */
    public static class IdentificationTypeFilter extends Filter<IdentificationType> {

        public IdentificationTypeFilter() {}

        public IdentificationTypeFilter(IdentificationTypeFilter filter) {
            super(filter);
        }

        @Override
        public IdentificationTypeFilter copy() {
            return new IdentificationTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private LocalDateFilter dateOfBirth;

    private CountryCodeFilter nationality;

    private IdentificationTypeFilter identificationType;

    private StringFilter identificationNumber;

    private LongFilter tenantId;

    private Boolean distinct;

    public TenantDirectorCriteria() {}

    public TenantDirectorCriteria(TenantDirectorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.dateOfBirth = other.optionalDateOfBirth().map(LocalDateFilter::copy).orElse(null);
        this.nationality = other.optionalNationality().map(CountryCodeFilter::copy).orElse(null);
        this.identificationType = other.optionalIdentificationType().map(IdentificationTypeFilter::copy).orElse(null);
        this.identificationNumber = other.optionalIdentificationNumber().map(StringFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantDirectorCriteria copy() {
        return new TenantDirectorCriteria(this);
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

    public StringFilter getFullName() {
        return fullName;
    }

    public Optional<StringFilter> optionalFullName() {
        return Optional.ofNullable(fullName);
    }

    public StringFilter fullName() {
        if (fullName == null) {
            setFullName(new StringFilter());
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public LocalDateFilter getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<LocalDateFilter> optionalDateOfBirth() {
        return Optional.ofNullable(dateOfBirth);
    }

    public LocalDateFilter dateOfBirth() {
        if (dateOfBirth == null) {
            setDateOfBirth(new LocalDateFilter());
        }
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateFilter dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public CountryCodeFilter getNationality() {
        return nationality;
    }

    public Optional<CountryCodeFilter> optionalNationality() {
        return Optional.ofNullable(nationality);
    }

    public CountryCodeFilter nationality() {
        if (nationality == null) {
            setNationality(new CountryCodeFilter());
        }
        return nationality;
    }

    public void setNationality(CountryCodeFilter nationality) {
        this.nationality = nationality;
    }

    public IdentificationTypeFilter getIdentificationType() {
        return identificationType;
    }

    public Optional<IdentificationTypeFilter> optionalIdentificationType() {
        return Optional.ofNullable(identificationType);
    }

    public IdentificationTypeFilter identificationType() {
        if (identificationType == null) {
            setIdentificationType(new IdentificationTypeFilter());
        }
        return identificationType;
    }

    public void setIdentificationType(IdentificationTypeFilter identificationType) {
        this.identificationType = identificationType;
    }

    public StringFilter getIdentificationNumber() {
        return identificationNumber;
    }

    public Optional<StringFilter> optionalIdentificationNumber() {
        return Optional.ofNullable(identificationNumber);
    }

    public StringFilter identificationNumber() {
        if (identificationNumber == null) {
            setIdentificationNumber(new StringFilter());
        }
        return identificationNumber;
    }

    public void setIdentificationNumber(StringFilter identificationNumber) {
        this.identificationNumber = identificationNumber;
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
        final TenantDirectorCriteria that = (TenantDirectorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(dateOfBirth, that.dateOfBirth) &&
            Objects.equals(nationality, that.nationality) &&
            Objects.equals(identificationType, that.identificationType) &&
            Objects.equals(identificationNumber, that.identificationNumber) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, dateOfBirth, nationality, identificationType, identificationNumber, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDirectorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalDateOfBirth().map(f -> "dateOfBirth=" + f + ", ").orElse("") +
            optionalNationality().map(f -> "nationality=" + f + ", ").orElse("") +
            optionalIdentificationType().map(f -> "identificationType=" + f + ", ").orElse("") +
            optionalIdentificationNumber().map(f -> "identificationNumber=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
