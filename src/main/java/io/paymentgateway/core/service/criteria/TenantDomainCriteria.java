package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.TenantDomain} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.TenantDomainResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenant-domains?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDomainCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter customDomain;

    private StringFilter supportedLocales;

    private StringFilter defaultLocale;

    private StringFilter fallbackLocale;

    private BooleanFilter isVerified;

    private LongFilter tenantId;

    private Boolean distinct;

    public TenantDomainCriteria() {}

    public TenantDomainCriteria(TenantDomainCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.customDomain = other.optionalCustomDomain().map(StringFilter::copy).orElse(null);
        this.supportedLocales = other.optionalSupportedLocales().map(StringFilter::copy).orElse(null);
        this.defaultLocale = other.optionalDefaultLocale().map(StringFilter::copy).orElse(null);
        this.fallbackLocale = other.optionalFallbackLocale().map(StringFilter::copy).orElse(null);
        this.isVerified = other.optionalIsVerified().map(BooleanFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantDomainCriteria copy() {
        return new TenantDomainCriteria(this);
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

    public StringFilter getCustomDomain() {
        return customDomain;
    }

    public Optional<StringFilter> optionalCustomDomain() {
        return Optional.ofNullable(customDomain);
    }

    public StringFilter customDomain() {
        if (customDomain == null) {
            setCustomDomain(new StringFilter());
        }
        return customDomain;
    }

    public void setCustomDomain(StringFilter customDomain) {
        this.customDomain = customDomain;
    }

    public StringFilter getSupportedLocales() {
        return supportedLocales;
    }

    public Optional<StringFilter> optionalSupportedLocales() {
        return Optional.ofNullable(supportedLocales);
    }

    public StringFilter supportedLocales() {
        if (supportedLocales == null) {
            setSupportedLocales(new StringFilter());
        }
        return supportedLocales;
    }

    public void setSupportedLocales(StringFilter supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public StringFilter getDefaultLocale() {
        return defaultLocale;
    }

    public Optional<StringFilter> optionalDefaultLocale() {
        return Optional.ofNullable(defaultLocale);
    }

    public StringFilter defaultLocale() {
        if (defaultLocale == null) {
            setDefaultLocale(new StringFilter());
        }
        return defaultLocale;
    }

    public void setDefaultLocale(StringFilter defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public StringFilter getFallbackLocale() {
        return fallbackLocale;
    }

    public Optional<StringFilter> optionalFallbackLocale() {
        return Optional.ofNullable(fallbackLocale);
    }

    public StringFilter fallbackLocale() {
        if (fallbackLocale == null) {
            setFallbackLocale(new StringFilter());
        }
        return fallbackLocale;
    }

    public void setFallbackLocale(StringFilter fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    public BooleanFilter getIsVerified() {
        return isVerified;
    }

    public Optional<BooleanFilter> optionalIsVerified() {
        return Optional.ofNullable(isVerified);
    }

    public BooleanFilter isVerified() {
        if (isVerified == null) {
            setIsVerified(new BooleanFilter());
        }
        return isVerified;
    }

    public void setIsVerified(BooleanFilter isVerified) {
        this.isVerified = isVerified;
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
        final TenantDomainCriteria that = (TenantDomainCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(customDomain, that.customDomain) &&
            Objects.equals(supportedLocales, that.supportedLocales) &&
            Objects.equals(defaultLocale, that.defaultLocale) &&
            Objects.equals(fallbackLocale, that.fallbackLocale) &&
            Objects.equals(isVerified, that.isVerified) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customDomain, supportedLocales, defaultLocale, fallbackLocale, isVerified, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDomainCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCustomDomain().map(f -> "customDomain=" + f + ", ").orElse("") +
            optionalSupportedLocales().map(f -> "supportedLocales=" + f + ", ").orElse("") +
            optionalDefaultLocale().map(f -> "defaultLocale=" + f + ", ").orElse("") +
            optionalFallbackLocale().map(f -> "fallbackLocale=" + f + ", ").orElse("") +
            optionalIsVerified().map(f -> "isVerified=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
