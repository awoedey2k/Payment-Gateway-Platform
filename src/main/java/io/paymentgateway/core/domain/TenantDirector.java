package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.IdentificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TenantDirector.
 */
@Entity
@Table(name = "tenant_director")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDirector implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    private CountryCode nationality;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false)
    private IdentificationType identificationType;

    @NotNull
    @Column(name = "identification_number", nullable = false)
    private String identificationNumber;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TenantDirector id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public TenantDirector fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public TenantDirector dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public CountryCode getNationality() {
        return this.nationality;
    }

    public TenantDirector nationality(CountryCode nationality) {
        this.setNationality(nationality);
        return this;
    }

    public void setNationality(CountryCode nationality) {
        this.nationality = nationality;
    }

    public IdentificationType getIdentificationType() {
        return this.identificationType;
    }

    public TenantDirector identificationType(IdentificationType identificationType) {
        this.setIdentificationType(identificationType);
        return this;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return this.identificationNumber;
    }

    public TenantDirector identificationNumber(String identificationNumber) {
        this.setIdentificationNumber(identificationNumber);
        return this;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public TenantDirector tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantDirector)) {
            return false;
        }
        return getId() != null && getId().equals(((TenantDirector) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDirector{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", identificationType='" + getIdentificationType() + "'" +
            ", identificationNumber='" + getIdentificationNumber() + "'" +
            "}";
    }
}
