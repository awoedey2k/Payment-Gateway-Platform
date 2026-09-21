package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.JournalEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalEntryDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private String description;

    @NotNull
    private Instant postedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalEntryDTO)) {
            return false;
        }

        JournalEntryDTO journalEntryDTO = (JournalEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, journalEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalEntryDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", description='" + getDescription() + "'" +
            ", postedAt='" + getPostedAt() + "'" +
            "}";
    }
}
