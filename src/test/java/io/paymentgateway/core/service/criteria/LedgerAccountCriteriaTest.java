package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LedgerAccountCriteriaTest {

    @Test
    void newLedgerAccountCriteriaHasAllFiltersNullTest() {
        var ledgerAccountCriteria = new LedgerAccountCriteria();
        assertThat(ledgerAccountCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ledgerAccountCriteriaFluentMethodsCreatesFiltersTest() {
        var ledgerAccountCriteria = new LedgerAccountCriteria();

        setAllFilters(ledgerAccountCriteria);

        assertThat(ledgerAccountCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ledgerAccountCriteriaCopyCreatesNullFilterTest() {
        var ledgerAccountCriteria = new LedgerAccountCriteria();
        var copy = ledgerAccountCriteria.copy();

        assertThat(ledgerAccountCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ledgerAccountCriteria)
        );
    }

    @Test
    void ledgerAccountCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ledgerAccountCriteria = new LedgerAccountCriteria();
        setAllFilters(ledgerAccountCriteria);

        var copy = ledgerAccountCriteria.copy();

        assertThat(ledgerAccountCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ledgerAccountCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ledgerAccountCriteria = new LedgerAccountCriteria();

        assertThat(ledgerAccountCriteria).hasToString("LedgerAccountCriteria{}");
    }

    private static void setAllFilters(LedgerAccountCriteria ledgerAccountCriteria) {
        ledgerAccountCriteria.id();
        ledgerAccountCriteria.accountCode();
        ledgerAccountCriteria.accountType();
        ledgerAccountCriteria.currencyCode();
        ledgerAccountCriteria.distinct();
    }

    private static Condition<LedgerAccountCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAccountCode()) &&
                condition.apply(criteria.getAccountType()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LedgerAccountCriteria> copyFiltersAre(
        LedgerAccountCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAccountCode(), copy.getAccountCode()) &&
                condition.apply(criteria.getAccountType(), copy.getAccountType()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
