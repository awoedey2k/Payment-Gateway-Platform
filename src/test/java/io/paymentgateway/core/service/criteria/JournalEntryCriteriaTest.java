package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class JournalEntryCriteriaTest {

    @Test
    void newJournalEntryCriteriaHasAllFiltersNullTest() {
        var journalEntryCriteria = new JournalEntryCriteria();
        assertThat(journalEntryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void journalEntryCriteriaFluentMethodsCreatesFiltersTest() {
        var journalEntryCriteria = new JournalEntryCriteria();

        setAllFilters(journalEntryCriteria);

        assertThat(journalEntryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void journalEntryCriteriaCopyCreatesNullFilterTest() {
        var journalEntryCriteria = new JournalEntryCriteria();
        var copy = journalEntryCriteria.copy();

        assertThat(journalEntryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(journalEntryCriteria)
        );
    }

    @Test
    void journalEntryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var journalEntryCriteria = new JournalEntryCriteria();
        setAllFilters(journalEntryCriteria);

        var copy = journalEntryCriteria.copy();

        assertThat(journalEntryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(journalEntryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var journalEntryCriteria = new JournalEntryCriteria();

        assertThat(journalEntryCriteria).hasToString("JournalEntryCriteria{}");
    }

    private static void setAllFilters(JournalEntryCriteria journalEntryCriteria) {
        journalEntryCriteria.id();
        journalEntryCriteria.reference();
        journalEntryCriteria.description();
        journalEntryCriteria.postedAt();
        journalEntryCriteria.journalLineId();
        journalEntryCriteria.distinct();
    }

    private static Condition<JournalEntryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getPostedAt()) &&
                condition.apply(criteria.getJournalLineId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<JournalEntryCriteria> copyFiltersAre(
        JournalEntryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getPostedAt(), copy.getPostedAt()) &&
                condition.apply(criteria.getJournalLineId(), copy.getJournalLineId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
