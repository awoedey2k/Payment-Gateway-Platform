package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CountryPaymentMethodCriteriaTest {

    @Test
    void newCountryPaymentMethodCriteriaHasAllFiltersNullTest() {
        var countryPaymentMethodCriteria = new CountryPaymentMethodCriteria();
        assertThat(countryPaymentMethodCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void countryPaymentMethodCriteriaFluentMethodsCreatesFiltersTest() {
        var countryPaymentMethodCriteria = new CountryPaymentMethodCriteria();

        setAllFilters(countryPaymentMethodCriteria);

        assertThat(countryPaymentMethodCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void countryPaymentMethodCriteriaCopyCreatesNullFilterTest() {
        var countryPaymentMethodCriteria = new CountryPaymentMethodCriteria();
        var copy = countryPaymentMethodCriteria.copy();

        assertThat(countryPaymentMethodCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(countryPaymentMethodCriteria)
        );
    }

    @Test
    void countryPaymentMethodCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var countryPaymentMethodCriteria = new CountryPaymentMethodCriteria();
        setAllFilters(countryPaymentMethodCriteria);

        var copy = countryPaymentMethodCriteria.copy();

        assertThat(countryPaymentMethodCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(countryPaymentMethodCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var countryPaymentMethodCriteria = new CountryPaymentMethodCriteria();

        assertThat(countryPaymentMethodCriteria).hasToString("CountryPaymentMethodCriteria{}");
    }

    private static void setAllFilters(CountryPaymentMethodCriteria countryPaymentMethodCriteria) {
        countryPaymentMethodCriteria.id();
        countryPaymentMethodCriteria.minTxnAmount();
        countryPaymentMethodCriteria.maxTxnAmount();
        countryPaymentMethodCriteria.supportsRecurring();
        countryPaymentMethodCriteria.supportsInstantRefund();
        countryPaymentMethodCriteria.isActive();
        countryPaymentMethodCriteria.countryId();
        countryPaymentMethodCriteria.paymentMethodId();
        countryPaymentMethodCriteria.distinct();
    }

    private static Condition<CountryPaymentMethodCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMinTxnAmount()) &&
                condition.apply(criteria.getMaxTxnAmount()) &&
                condition.apply(criteria.getSupportsRecurring()) &&
                condition.apply(criteria.getSupportsInstantRefund()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getCountryId()) &&
                condition.apply(criteria.getPaymentMethodId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CountryPaymentMethodCriteria> copyFiltersAre(
        CountryPaymentMethodCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMinTxnAmount(), copy.getMinTxnAmount()) &&
                condition.apply(criteria.getMaxTxnAmount(), copy.getMaxTxnAmount()) &&
                condition.apply(criteria.getSupportsRecurring(), copy.getSupportsRecurring()) &&
                condition.apply(criteria.getSupportsInstantRefund(), copy.getSupportsInstantRefund()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getCountryId(), copy.getCountryId()) &&
                condition.apply(criteria.getPaymentMethodId(), copy.getPaymentMethodId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
