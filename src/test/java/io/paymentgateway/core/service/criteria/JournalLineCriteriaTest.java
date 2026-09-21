package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class JournalLineCriteriaTest {

    @Test
    void newJournalLineCriteriaHasAllFiltersNullTest() {
        var journalLineCriteria = new JournalLineCriteria();
        assertThat(journalLineCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void journalLineCriteriaFluentMethodsCreatesFiltersTest() {
        var journalLineCriteria = new JournalLineCriteria();

        setAllFilters(journalLineCriteria);

        assertThat(journalLineCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void journalLineCriteriaCopyCreatesNullFilterTest() {
        var journalLineCriteria = new JournalLineCriteria();
        var copy = journalLineCriteria.copy();

        assertThat(journalLineCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(journalLineCriteria)
        );
    }

    @Test
    void journalLineCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var journalLineCriteria = new JournalLineCriteria();
        setAllFilters(journalLineCriteria);

        var copy = journalLineCriteria.copy();

        assertThat(journalLineCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(journalLineCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var journalLineCriteria = new JournalLineCriteria();

        assertThat(journalLineCriteria).hasToString("JournalLineCriteria{}");
    }

    private static void setAllFilters(JournalLineCriteria journalLineCriteria) {
        journalLineCriteria.id();
        journalLineCriteria.debitAmount();
        journalLineCriteria.creditAmount();
        journalLineCriteria.accountId();
        journalLineCriteria.journalEntryId();
        journalLineCriteria.distinct();
    }

    private static Condition<JournalLineCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDebitAmount()) &&
                condition.apply(criteria.getCreditAmount()) &&
                condition.apply(criteria.getAccountId()) &&
                condition.apply(criteria.getJournalEntryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<JournalLineCriteria> copyFiltersAre(JournalLineCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDebitAmount(), copy.getDebitAmount()) &&
                condition.apply(criteria.getCreditAmount(), copy.getCreditAmount()) &&
                condition.apply(criteria.getAccountId(), copy.getAccountId()) &&
                condition.apply(criteria.getJournalEntryId(), copy.getJournalEntryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
