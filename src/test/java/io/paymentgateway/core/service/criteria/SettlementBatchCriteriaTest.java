package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SettlementBatchCriteriaTest {

    @Test
    void newSettlementBatchCriteriaHasAllFiltersNullTest() {
        var settlementBatchCriteria = new SettlementBatchCriteria();
        assertThat(settlementBatchCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void settlementBatchCriteriaFluentMethodsCreatesFiltersTest() {
        var settlementBatchCriteria = new SettlementBatchCriteria();

        setAllFilters(settlementBatchCriteria);

        assertThat(settlementBatchCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void settlementBatchCriteriaCopyCreatesNullFilterTest() {
        var settlementBatchCriteria = new SettlementBatchCriteria();
        var copy = settlementBatchCriteria.copy();

        assertThat(settlementBatchCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(settlementBatchCriteria)
        );
    }

    @Test
    void settlementBatchCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var settlementBatchCriteria = new SettlementBatchCriteria();
        setAllFilters(settlementBatchCriteria);

        var copy = settlementBatchCriteria.copy();

        assertThat(settlementBatchCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(settlementBatchCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var settlementBatchCriteria = new SettlementBatchCriteria();

        assertThat(settlementBatchCriteria).hasToString("SettlementBatchCriteria{}");
    }

    private static void setAllFilters(SettlementBatchCriteria settlementBatchCriteria) {
        settlementBatchCriteria.id();
        settlementBatchCriteria.reference();
        settlementBatchCriteria.status();
        settlementBatchCriteria.totalAmount();
        settlementBatchCriteria.currencyCode();
        settlementBatchCriteria.scheduledAt();
        settlementBatchCriteria.completedAt();
        settlementBatchCriteria.tenantId();
        settlementBatchCriteria.distinct();
    }

    private static Condition<SettlementBatchCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getScheduledAt()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SettlementBatchCriteria> copyFiltersAre(
        SettlementBatchCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getScheduledAt(), copy.getScheduledAt()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
