package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.IdentificationType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.TenantDirector} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDirectorDTO implements Serializable {

    private Long id;

    @NotNull
    private String fullName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private CountryCode nationality;

    @NotNull
    private IdentificationType identificationType;

    @NotNull
    private String identificationNumber;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public CountryCode getNationality() {
        return nationality;
    }

    public void setNationality(CountryCode nationality) {
        this.nationality = nationality;
    }

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantDirectorDTO)) {
            return false;
        }

        TenantDirectorDTO tenantDirectorDTO = (TenantDirectorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tenantDirectorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDirectorDTO{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", identificationType='" + getIdentificationType() + "'" +
            ", identificationNumber='" + getIdentificationNumber() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
