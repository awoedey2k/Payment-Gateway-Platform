package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PayoutScheduleCriteriaTest {

    @Test
    void newPayoutScheduleCriteriaHasAllFiltersNullTest() {
        var payoutScheduleCriteria = new PayoutScheduleCriteria();
        assertThat(payoutScheduleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void payoutScheduleCriteriaFluentMethodsCreatesFiltersTest() {
        var payoutScheduleCriteria = new PayoutScheduleCriteria();

        setAllFilters(payoutScheduleCriteria);

        assertThat(payoutScheduleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void payoutScheduleCriteriaCopyCreatesNullFilterTest() {
        var payoutScheduleCriteria = new PayoutScheduleCriteria();
        var copy = payoutScheduleCriteria.copy();

        assertThat(payoutScheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(payoutScheduleCriteria)
        );
    }

    @Test
    void payoutScheduleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var payoutScheduleCriteria = new PayoutScheduleCriteria();
        setAllFilters(payoutScheduleCriteria);

        var copy = payoutScheduleCriteria.copy();

        assertThat(payoutScheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(payoutScheduleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var payoutScheduleCriteria = new PayoutScheduleCriteria();

        assertThat(payoutScheduleCriteria).hasToString("PayoutScheduleCriteria{}");
    }

    private static void setAllFilters(PayoutScheduleCriteria payoutScheduleCriteria) {
        payoutScheduleCriteria.id();
        payoutScheduleCriteria.frequencyMode();
        payoutScheduleCriteria.thresholdAmount();
        payoutScheduleCriteria.isActive();
        payoutScheduleCriteria.tenantId();
        payoutScheduleCriteria.distinct();
    }

    private static Condition<PayoutScheduleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFrequencyMode()) &&
                condition.apply(criteria.getThresholdAmount()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PayoutScheduleCriteria> copyFiltersAre(
        PayoutScheduleCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFrequencyMode(), copy.getFrequencyMode()) &&
                condition.apply(criteria.getThresholdAmount(), copy.getThresholdAmount()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
