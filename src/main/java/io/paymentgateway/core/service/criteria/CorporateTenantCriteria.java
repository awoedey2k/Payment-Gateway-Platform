package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.CorporateTenant} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.CorporateTenantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /corporate-tenants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorporateTenantCriteria implements Serializable, Criteria {

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
     * Class for filtering TenantStatus
     */
    public static class TenantStatusFilter extends Filter<TenantStatus> {

        public TenantStatusFilter() {}

        public TenantStatusFilter(TenantStatusFilter filter) {
            super(filter);
        }

        @Override
        public TenantStatusFilter copy() {
            return new TenantStatusFilter(this);
        }
    }

    /**
     * Class for filtering KycStatus
     */
    public static class KycStatusFilter extends Filter<KycStatus> {

        public KycStatusFilter() {}

        public KycStatusFilter(KycStatusFilter filter) {
            super(filter);
        }

        @Override
        public KycStatusFilter copy() {
            return new KycStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter legalBusinessName;

    private StringFilter businessRegistrationNumber;

    private StringFilter taxIdentificationNumber;

    private CountryCodeFilter operatingJurisdiction;

    private TenantStatusFilter status;

    private KycStatusFilter kycStatus;

    private IntegerFilter riskScore;

    private InstantFilter createdAt;

    private InstantFilter activatedAt;

    private LongFilter tenantDirectorId;

    private LongFilter apiKeyId;

    private LongFilter tenantDomainId;

    private Boolean distinct;

    public CorporateTenantCriteria() {}

    public CorporateTenantCriteria(CorporateTenantCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.legalBusinessName = other.optionalLegalBusinessName().map(StringFilter::copy).orElse(null);
        this.businessRegistrationNumber = other.optionalBusinessRegistrationNumber().map(StringFilter::copy).orElse(null);
        this.taxIdentificationNumber = other.optionalTaxIdentificationNumber().map(StringFilter::copy).orElse(null);
        this.operatingJurisdiction = other.optionalOperatingJurisdiction().map(CountryCodeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TenantStatusFilter::copy).orElse(null);
        this.kycStatus = other.optionalKycStatus().map(KycStatusFilter::copy).orElse(null);
        this.riskScore = other.optionalRiskScore().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.activatedAt = other.optionalActivatedAt().map(InstantFilter::copy).orElse(null);
        this.tenantDirectorId = other.optionalTenantDirectorId().map(LongFilter::copy).orElse(null);
        this.apiKeyId = other.optionalApiKeyId().map(LongFilter::copy).orElse(null);
        this.tenantDomainId = other.optionalTenantDomainId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CorporateTenantCriteria copy() {
        return new CorporateTenantCriteria(this);
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

    public StringFilter getLegalBusinessName() {
        return legalBusinessName;
    }

    public Optional<StringFilter> optionalLegalBusinessName() {
        return Optional.ofNullable(legalBusinessName);
    }

    public StringFilter legalBusinessName() {
        if (legalBusinessName == null) {
            setLegalBusinessName(new StringFilter());
        }
        return legalBusinessName;
    }

    public void setLegalBusinessName(StringFilter legalBusinessName) {
        this.legalBusinessName = legalBusinessName;
    }

    public StringFilter getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public Optional<StringFilter> optionalBusinessRegistrationNumber() {
        return Optional.ofNullable(businessRegistrationNumber);
    }

    public StringFilter businessRegistrationNumber() {
        if (businessRegistrationNumber == null) {
            setBusinessRegistrationNumber(new StringFilter());
        }
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(StringFilter businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public StringFilter getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    public Optional<StringFilter> optionalTaxIdentificationNumber() {
        return Optional.ofNullable(taxIdentificationNumber);
    }

    public StringFilter taxIdentificationNumber() {
        if (taxIdentificationNumber == null) {
            setTaxIdentificationNumber(new StringFilter());
        }
        return taxIdentificationNumber;
    }

    public void setTaxIdentificationNumber(StringFilter taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public CountryCodeFilter getOperatingJurisdiction() {
        return operatingJurisdiction;
    }

    public Optional<CountryCodeFilter> optionalOperatingJurisdiction() {
        return Optional.ofNullable(operatingJurisdiction);
    }

    public CountryCodeFilter operatingJurisdiction() {
        if (operatingJurisdiction == null) {
            setOperatingJurisdiction(new CountryCodeFilter());
        }
        return operatingJurisdiction;
    }

    public void setOperatingJurisdiction(CountryCodeFilter operatingJurisdiction) {
        this.operatingJurisdiction = operatingJurisdiction;
    }

    public TenantStatusFilter getStatus() {
        return status;
    }

    public Optional<TenantStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TenantStatusFilter status() {
        if (status == null) {
            setStatus(new TenantStatusFilter());
        }
        return status;
    }

    public void setStatus(TenantStatusFilter status) {
        this.status = status;
    }

    public KycStatusFilter getKycStatus() {
        return kycStatus;
    }

    public Optional<KycStatusFilter> optionalKycStatus() {
        return Optional.ofNullable(kycStatus);
    }

    public KycStatusFilter kycStatus() {
        if (kycStatus == null) {
            setKycStatus(new KycStatusFilter());
        }
        return kycStatus;
    }

    public void setKycStatus(KycStatusFilter kycStatus) {
        this.kycStatus = kycStatus;
    }

    public IntegerFilter getRiskScore() {
        return riskScore;
    }

    public Optional<IntegerFilter> optionalRiskScore() {
        return Optional.ofNullable(riskScore);
    }

    public IntegerFilter riskScore() {
        if (riskScore == null) {
            setRiskScore(new IntegerFilter());
        }
        return riskScore;
    }

    public void setRiskScore(IntegerFilter riskScore) {
        this.riskScore = riskScore;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getActivatedAt() {
        return activatedAt;
    }

    public Optional<InstantFilter> optionalActivatedAt() {
        return Optional.ofNullable(activatedAt);
    }

    public InstantFilter activatedAt() {
        if (activatedAt == null) {
            setActivatedAt(new InstantFilter());
        }
        return activatedAt;
    }

    public void setActivatedAt(InstantFilter activatedAt) {
        this.activatedAt = activatedAt;
    }

    public LongFilter getTenantDirectorId() {
        return tenantDirectorId;
    }

    public Optional<LongFilter> optionalTenantDirectorId() {
        return Optional.ofNullable(tenantDirectorId);
    }

    public LongFilter tenantDirectorId() {
        if (tenantDirectorId == null) {
            setTenantDirectorId(new LongFilter());
        }
        return tenantDirectorId;
    }

    public void setTenantDirectorId(LongFilter tenantDirectorId) {
        this.tenantDirectorId = tenantDirectorId;
    }

    public LongFilter getApiKeyId() {
        return apiKeyId;
    }

    public Optional<LongFilter> optionalApiKeyId() {
        return Optional.ofNullable(apiKeyId);
    }

    public LongFilter apiKeyId() {
        if (apiKeyId == null) {
            setApiKeyId(new LongFilter());
        }
        return apiKeyId;
    }

    public void setApiKeyId(LongFilter apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    public LongFilter getTenantDomainId() {
        return tenantDomainId;
    }

    public Optional<LongFilter> optionalTenantDomainId() {
        return Optional.ofNullable(tenantDomainId);
    }

    public LongFilter tenantDomainId() {
        if (tenantDomainId == null) {
            setTenantDomainId(new LongFilter());
        }
        return tenantDomainId;
    }

    public void setTenantDomainId(LongFilter tenantDomainId) {
        this.tenantDomainId = tenantDomainId;
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
        final CorporateTenantCriteria that = (CorporateTenantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(legalBusinessName, that.legalBusinessName) &&
            Objects.equals(businessRegistrationNumber, that.businessRegistrationNumber) &&
            Objects.equals(taxIdentificationNumber, that.taxIdentificationNumber) &&
            Objects.equals(operatingJurisdiction, that.operatingJurisdiction) &&
            Objects.equals(status, that.status) &&
            Objects.equals(kycStatus, that.kycStatus) &&
            Objects.equals(riskScore, that.riskScore) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(activatedAt, that.activatedAt) &&
            Objects.equals(tenantDirectorId, that.tenantDirectorId) &&
            Objects.equals(apiKeyId, that.apiKeyId) &&
            Objects.equals(tenantDomainId, that.tenantDomainId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            legalBusinessName,
            businessRegistrationNumber,
            taxIdentificationNumber,
            operatingJurisdiction,
            status,
            kycStatus,
            riskScore,
            createdAt,
            activatedAt,
            tenantDirectorId,
            apiKeyId,
            tenantDomainId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorporateTenantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLegalBusinessName().map(f -> "legalBusinessName=" + f + ", ").orElse("") +
            optionalBusinessRegistrationNumber().map(f -> "businessRegistrationNumber=" + f + ", ").orElse("") +
            optionalTaxIdentificationNumber().map(f -> "taxIdentificationNumber=" + f + ", ").orElse("") +
            optionalOperatingJurisdiction().map(f -> "operatingJurisdiction=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalKycStatus().map(f -> "kycStatus=" + f + ", ").orElse("") +
            optionalRiskScore().map(f -> "riskScore=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalActivatedAt().map(f -> "activatedAt=" + f + ", ").orElse("") +
            optionalTenantDirectorId().map(f -> "tenantDirectorId=" + f + ", ").orElse("") +
            optionalApiKeyId().map(f -> "apiKeyId=" + f + ", ").orElse("") +
            optionalTenantDomainId().map(f -> "tenantDomainId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
