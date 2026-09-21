package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.JournalLine} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.JournalLineResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /journal-lines?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalLineCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter debitAmount;

    private BigDecimalFilter creditAmount;

    private LongFilter accountId;

    private LongFilter journalEntryId;

    private Boolean distinct;

    public JournalLineCriteria() {}

    public JournalLineCriteria(JournalLineCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.debitAmount = other.optionalDebitAmount().map(BigDecimalFilter::copy).orElse(null);
        this.creditAmount = other.optionalCreditAmount().map(BigDecimalFilter::copy).orElse(null);
        this.accountId = other.optionalAccountId().map(LongFilter::copy).orElse(null);
        this.journalEntryId = other.optionalJournalEntryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public JournalLineCriteria copy() {
        return new JournalLineCriteria(this);
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

    public BigDecimalFilter getDebitAmount() {
        return debitAmount;
    }

    public Optional<BigDecimalFilter> optionalDebitAmount() {
        return Optional.ofNullable(debitAmount);
    }

    public BigDecimalFilter debitAmount() {
        if (debitAmount == null) {
            setDebitAmount(new BigDecimalFilter());
        }
        return debitAmount;
    }

    public void setDebitAmount(BigDecimalFilter debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimalFilter getCreditAmount() {
        return creditAmount;
    }

    public Optional<BigDecimalFilter> optionalCreditAmount() {
        return Optional.ofNullable(creditAmount);
    }

    public BigDecimalFilter creditAmount() {
        if (creditAmount == null) {
            setCreditAmount(new BigDecimalFilter());
        }
        return creditAmount;
    }

    public void setCreditAmount(BigDecimalFilter creditAmount) {
        this.creditAmount = creditAmount;
    }

    public LongFilter getAccountId() {
        return accountId;
    }

    public Optional<LongFilter> optionalAccountId() {
        return Optional.ofNullable(accountId);
    }

    public LongFilter accountId() {
        if (accountId == null) {
            setAccountId(new LongFilter());
        }
        return accountId;
    }

    public void setAccountId(LongFilter accountId) {
        this.accountId = accountId;
    }

    public LongFilter getJournalEntryId() {
        return journalEntryId;
    }

    public Optional<LongFilter> optionalJournalEntryId() {
        return Optional.ofNullable(journalEntryId);
    }

    public LongFilter journalEntryId() {
        if (journalEntryId == null) {
            setJournalEntryId(new LongFilter());
        }
        return journalEntryId;
    }

    public void setJournalEntryId(LongFilter journalEntryId) {
        this.journalEntryId = journalEntryId;
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
        final JournalLineCriteria that = (JournalLineCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(debitAmount, that.debitAmount) &&
            Objects.equals(creditAmount, that.creditAmount) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(journalEntryId, that.journalEntryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, debitAmount, creditAmount, accountId, journalEntryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalLineCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDebitAmount().map(f -> "debitAmount=" + f + ", ").orElse("") +
            optionalCreditAmount().map(f -> "creditAmount=" + f + ", ").orElse("") +
            optionalAccountId().map(f -> "accountId=" + f + ", ").orElse("") +
            optionalJournalEntryId().map(f -> "journalEntryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
