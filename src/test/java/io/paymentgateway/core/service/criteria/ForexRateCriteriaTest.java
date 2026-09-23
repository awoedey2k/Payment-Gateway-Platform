package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ForexRateCriteriaTest {

    @Test
    void newForexRateCriteriaHasAllFiltersNullTest() {
        var forexRateCriteria = new ForexRateCriteria();
        assertThat(forexRateCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void forexRateCriteriaFluentMethodsCreatesFiltersTest() {
        var forexRateCriteria = new ForexRateCriteria();

        setAllFilters(forexRateCriteria);

        assertThat(forexRateCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void forexRateCriteriaCopyCreatesNullFilterTest() {
        var forexRateCriteria = new ForexRateCriteria();
        var copy = forexRateCriteria.copy();

        assertThat(forexRateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(forexRateCriteria)
        );
    }

    @Test
    void forexRateCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var forexRateCriteria = new ForexRateCriteria();
        setAllFilters(forexRateCriteria);

        var copy = forexRateCriteria.copy();

        assertThat(forexRateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(forexRateCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var forexRateCriteria = new ForexRateCriteria();

        assertThat(forexRateCriteria).hasToString("ForexRateCriteria{}");
    }

    private static void setAllFilters(ForexRateCriteria forexRateCriteria) {
        forexRateCriteria.id();
        forexRateCriteria.baseCurrency();
        forexRateCriteria.quoteCurrency();
        forexRateCriteria.rate();
        forexRateCriteria.platformSpreadBps();
        forexRateCriteria.lockedAt();
        forexRateCriteria.expiresAt();
        forexRateCriteria.distinct();
    }

    private static Condition<ForexRateCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBaseCurrency()) &&
                condition.apply(criteria.getQuoteCurrency()) &&
                condition.apply(criteria.getRate()) &&
                condition.apply(criteria.getPlatformSpreadBps()) &&
                condition.apply(criteria.getLockedAt()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ForexRateCriteria> copyFiltersAre(ForexRateCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBaseCurrency(), copy.getBaseCurrency()) &&
                condition.apply(criteria.getQuoteCurrency(), copy.getQuoteCurrency()) &&
                condition.apply(criteria.getRate(), copy.getRate()) &&
                condition.apply(criteria.getPlatformSpreadBps(), copy.getPlatformSpreadBps()) &&
                condition.apply(criteria.getLockedAt(), copy.getLockedAt()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
