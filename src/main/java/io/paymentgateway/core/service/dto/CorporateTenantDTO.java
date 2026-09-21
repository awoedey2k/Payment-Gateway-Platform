package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.CorporateTenant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorporateTenantDTO implements Serializable {

    private Long id;

    @NotNull
    private String legalBusinessName;

    @NotNull
    private String businessRegistrationNumber;

    @NotNull
    private String taxIdentificationNumber;

    @NotNull
    private CountryCode operatingJurisdiction;

    @NotNull
    private TenantStatus status;

    @NotNull
    private KycStatus kycStatus;

    private Integer riskScore;

    @NotNull
    private Instant createdAt;

    private Instant activatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLegalBusinessName() {
        return legalBusinessName;
    }

    public void setLegalBusinessName(String legalBusinessName) {
        this.legalBusinessName = legalBusinessName;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    public void setTaxIdentificationNumber(String taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public CountryCode getOperatingJurisdiction() {
        return operatingJurisdiction;
    }

    public void setOperatingJurisdiction(CountryCode operatingJurisdiction) {
        this.operatingJurisdiction = operatingJurisdiction;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CorporateTenantDTO)) {
            return false;
        }

        CorporateTenantDTO corporateTenantDTO = (CorporateTenantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, corporateTenantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorporateTenantDTO{" +
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
