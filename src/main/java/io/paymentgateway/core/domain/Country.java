package io.paymentgateway.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Country.
 */
@Entity
@Table(name = "country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 2)
    @Column(name = "iso_code", length = 2, nullable = false, unique = true)
    private String isoCode;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "default_currency_code", nullable = false)
    private String defaultCurrencyCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Country id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsoCode() {
        return this.isoCode;
    }

    public Country isoCode(String isoCode) {
        this.setIsoCode(isoCode);
        return this;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return this.name;
    }

    public Country name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultCurrencyCode() {
        return this.defaultCurrencyCode;
    }

    public Country defaultCurrencyCode(String defaultCurrencyCode) {
        this.setDefaultCurrencyCode(defaultCurrencyCode);
        return this;
    }

    public void setDefaultCurrencyCode(String defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return getId() != null && getId().equals(((Country) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", isoCode='" + getIsoCode() + "'" +
            ", name='" + getName() + "'" +
            ", defaultCurrencyCode='" + getDefaultCurrencyCode() + "'" +
            "}";
    }
}
