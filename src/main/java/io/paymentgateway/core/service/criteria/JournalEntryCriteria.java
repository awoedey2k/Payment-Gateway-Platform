package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.JournalEntry} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.JournalEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /journal-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalEntryCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter description;

    private InstantFilter postedAt;

    private LongFilter journalLineId;

    private Boolean distinct;

    public JournalEntryCriteria() {}

    public JournalEntryCriteria(JournalEntryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.postedAt = other.optionalPostedAt().map(InstantFilter::copy).orElse(null);
        this.journalLineId = other.optionalJournalLineId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public JournalEntryCriteria copy() {
        return new JournalEntryCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public InstantFilter getPostedAt() {
        return postedAt;
    }

    public Optional<InstantFilter> optionalPostedAt() {
        return Optional.ofNullable(postedAt);
    }

    public InstantFilter postedAt() {
        if (postedAt == null) {
            setPostedAt(new InstantFilter());
        }
        return postedAt;
    }

    public void setPostedAt(InstantFilter postedAt) {
        this.postedAt = postedAt;
    }

    public LongFilter getJournalLineId() {
        return journalLineId;
    }

    public Optional<LongFilter> optionalJournalLineId() {
        return Optional.ofNullable(journalLineId);
    }

    public LongFilter journalLineId() {
        if (journalLineId == null) {
            setJournalLineId(new LongFilter());
        }
        return journalLineId;
    }

    public void setJournalLineId(LongFilter journalLineId) {
        this.journalLineId = journalLineId;
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
        final JournalEntryCriteria that = (JournalEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(description, that.description) &&
            Objects.equals(postedAt, that.postedAt) &&
            Objects.equals(journalLineId, that.journalLineId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, description, postedAt, journalLineId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalEntryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalPostedAt().map(f -> "postedAt=" + f + ", ").orElse("") +
            optionalJournalLineId().map(f -> "journalLineId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
