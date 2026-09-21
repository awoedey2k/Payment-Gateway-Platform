package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CorporateTenant.
 */
@Entity
@Table(name = "corporate_tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorporateTenant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "legal_business_name", nullable = false)
    private String legalBusinessName;

    @NotNull
    @Column(name = "business_registration_number", nullable = false, unique = true)
    private String businessRegistrationNumber;

    @NotNull
    @Column(name = "tax_identification_number", nullable = false)
    private String taxIdentificationNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operating_jurisdiction", nullable = false)
    private CountryCode operatingJurisdiction;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus;

    @Column(name = "risk_score")
    private Integer riskScore;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<TenantDirector> tenantDirectors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<ApiKey> apiKeys = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<TenantDomain> tenantDomains = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CorporateTenant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLegalBusinessName() {
        return this.legalBusinessName;
    }

    public CorporateTenant legalBusinessName(String legalBusinessName) {
        this.setLegalBusinessName(legalBusinessName);
        return this;
    }

    public void setLegalBusinessName(String legalBusinessName) {
        this.legalBusinessName = legalBusinessName;
    }

    public String getBusinessRegistrationNumber() {
        return this.businessRegistrationNumber;
    }

    public CorporateTenant businessRegistrationNumber(String businessRegistrationNumber) {
        this.setBusinessRegistrationNumber(businessRegistrationNumber);
        return this;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getTaxIdentificationNumber() {
        return this.taxIdentificationNumber;
    }

    public CorporateTenant taxIdentificationNumber(String taxIdentificationNumber) {
        this.setTaxIdentificationNumber(taxIdentificationNumber);
        return this;
    }

    public void setTaxIdentificationNumber(String taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public CountryCode getOperatingJurisdiction() {
        return this.operatingJurisdiction;
    }

    public CorporateTenant operatingJurisdiction(CountryCode operatingJurisdiction) {
        this.setOperatingJurisdiction(operatingJurisdiction);
        return this;
    }

    public void setOperatingJurisdiction(CountryCode operatingJurisdiction) {
        this.operatingJurisdiction = operatingJurisdiction;
    }

    public TenantStatus getStatus() {
        return this.status;
    }

    public CorporateTenant status(TenantStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public KycStatus getKycStatus() {
        return this.kycStatus;
    }

    public CorporateTenant kycStatus(KycStatus kycStatus) {
        this.setKycStatus(kycStatus);
        return this;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Integer getRiskScore() {
        return this.riskScore;
    }

    public CorporateTenant riskScore(Integer riskScore) {
        this.setRiskScore(riskScore);
        return this;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CorporateTenant createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getActivatedAt() {
        return this.activatedAt;
    }

    public CorporateTenant activatedAt(Instant activatedAt) {
        this.setActivatedAt(activatedAt);
        return this;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Set<TenantDirector> getTenantDirectors() {
        return this.tenantDirectors;
    }

    public void setTenantDirectors(Set<TenantDirector> tenantDirectors) {
        if (this.tenantDirectors != null) {
            this.tenantDirectors.forEach(i -> i.setTenant(null));
        }
        if (tenantDirectors != null) {
            tenantDirectors.forEach(i -> i.setTenant(this));
        }
        this.tenantDirectors = tenantDirectors;
    }

    public CorporateTenant tenantDirectors(Set<TenantDirector> tenantDirectors) {
        this.setTenantDirectors(tenantDirectors);
        return this;
    }

    public CorporateTenant addTenantDirector(TenantDirector tenantDirector) {
        this.tenantDirectors.add(tenantDirector);
        tenantDirector.setTenant(this);
        return this;
    }

    public CorporateTenant removeTenantDirector(TenantDirector tenantDirector) {
        this.tenantDirectors.remove(tenantDirector);
        tenantDirector.setTenant(null);
        return this;
    }

    public Set<ApiKey> getApiKeys() {
        return this.apiKeys;
    }

    public void setApiKeys(Set<ApiKey> apiKeys) {
        if (this.apiKeys != null) {
            this.apiKeys.forEach(i -> i.setTenant(null));
        }
        if (apiKeys != null) {
            apiKeys.forEach(i -> i.setTenant(this));
        }
        this.apiKeys = apiKeys;
    }

    public CorporateTenant apiKeys(Set<ApiKey> apiKeys) {
        this.setApiKeys(apiKeys);
        return this;
    }

    public CorporateTenant addApiKey(ApiKey apiKey) {
        this.apiKeys.add(apiKey);
        apiKey.setTenant(this);
        return this;
    }

    public CorporateTenant removeApiKey(ApiKey apiKey) {
        this.apiKeys.remove(apiKey);
        apiKey.setTenant(null);
        return this;
    }

    public Set<TenantDomain> getTenantDomains() {
        return this.tenantDomains;
    }

    public void setTenantDomains(Set<TenantDomain> tenantDomains) {
        if (this.tenantDomains != null) {
            this.tenantDomains.forEach(i -> i.setTenant(null));
        }
        if (tenantDomains != null) {
            tenantDomains.forEach(i -> i.setTenant(this));
        }
        this.tenantDomains = tenantDomains;
    }

    public CorporateTenant tenantDomains(Set<TenantDomain> tenantDomains) {
        this.setTenantDomains(tenantDomains);
        return this;
    }

    public CorporateTenant addTenantDomain(TenantDomain tenantDomain) {
        this.tenantDomains.add(tenantDomain);
        tenantDomain.setTenant(this);
        return this;
    }

    public CorporateTenant removeTenantDomain(TenantDomain tenantDomain) {
        this.tenantDomains.remove(tenantDomain);
        tenantDomain.setTenant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CorporateTenant)) {
            return false;
        }
        return getId() != null && getId().equals(((CorporateTenant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorporateTenant{" +
            "id=" + getId() +
            ", legalBusinessName='" + getLegalBusinessName() + "'" +
            ", businessRegistrationNumber='" + getBusinessRegistrationNumber() + "'" +
            ", taxIdentificationNumber='" + getTaxIdentificationNumber() + "'" +
            ", operatingJurisdiction='" + getOperatingJurisdiction() + "'" +
            ", status='" + getStatus() + "'" +
            ", kycStatus='" + getKycStatus() + "'" +
            ", riskScore=" + getRiskScore() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            "}";
    }
}
