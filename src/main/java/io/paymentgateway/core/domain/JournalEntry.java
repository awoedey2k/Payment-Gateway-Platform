package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * A JournalEntry.
 */
@Entity
@Table(name = "journal_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalEntry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "posted_at", nullable = false)
    private Instant postedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "journalEntry")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "account", "journalEntry" }, allowSetters = true)
    private Set<JournalLine> journalLines = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JournalEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public JournalEntry reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return this.description;
    }

    public JournalEntry description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getPostedAt() {
        return this.postedAt;
    }

    public JournalEntry postedAt(Instant postedAt) {
        this.setPostedAt(postedAt);
        return this;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    public Set<JournalLine> getJournalLines() {
        return this.journalLines;
    }

    public void setJournalLines(Set<JournalLine> journalLines) {
        if (this.journalLines != null) {
            this.journalLines.forEach(i -> i.setJournalEntry(null));
        }
        if (journalLines != null) {
            journalLines.forEach(i -> i.setJournalEntry(this));
        }
        this.journalLines = journalLines;
    }

    public JournalEntry journalLines(Set<JournalLine> journalLines) {
        this.setJournalLines(journalLines);
        return this;
    }

    public JournalEntry addJournalLine(JournalLine journalLine) {
        this.journalLines.add(journalLine);
        journalLine.setJournalEntry(this);
        return this;
    }

    public JournalEntry removeJournalLine(JournalLine journalLine) {
        this.journalLines.remove(journalLine);
        journalLine.setJournalEntry(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalEntry)) {
            return false;
        }
        return getId() != null && getId().equals(((JournalEntry) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalEntry{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", description='" + getDescription() + "'" +
            ", postedAt='" + getPostedAt() + "'" +
            "}";
    }
}
