package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AmlCheckCriteriaTest {

    @Test
    void newAmlCheckCriteriaHasAllFiltersNullTest() {
        var amlCheckCriteria = new AmlCheckCriteria();
        assertThat(amlCheckCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void amlCheckCriteriaFluentMethodsCreatesFiltersTest() {
        var amlCheckCriteria = new AmlCheckCriteria();

        setAllFilters(amlCheckCriteria);

        assertThat(amlCheckCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void amlCheckCriteriaCopyCreatesNullFilterTest() {
        var amlCheckCriteria = new AmlCheckCriteria();
        var copy = amlCheckCriteria.copy();

        assertThat(amlCheckCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(amlCheckCriteria)
        );
    }

    @Test
    void amlCheckCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var amlCheckCriteria = new AmlCheckCriteria();
        setAllFilters(amlCheckCriteria);

        var copy = amlCheckCriteria.copy();

        assertThat(amlCheckCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(amlCheckCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var amlCheckCriteria = new AmlCheckCriteria();

        assertThat(amlCheckCriteria).hasToString("AmlCheckCriteria{}");
    }

    private static void setAllFilters(AmlCheckCriteria amlCheckCriteria) {
        amlCheckCriteria.id();
        amlCheckCriteria.riskScore();
        amlCheckCriteria.decision();
        amlCheckCriteria.ruleTriggered();
        amlCheckCriteria.checkedAt();
        amlCheckCriteria.tenantId();
        amlCheckCriteria.transactionId();
        amlCheckCriteria.distinct();
    }

    private static Condition<AmlCheckCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRiskScore()) &&
                condition.apply(criteria.getDecision()) &&
                condition.apply(criteria.getRuleTriggered()) &&
                condition.apply(criteria.getCheckedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getTransactionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AmlCheckCriteria> copyFiltersAre(AmlCheckCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRiskScore(), copy.getRiskScore()) &&
                condition.apply(criteria.getDecision(), copy.getDecision()) &&
                condition.apply(criteria.getRuleTriggered(), copy.getRuleTriggered()) &&
                condition.apply(criteria.getCheckedAt(), copy.getCheckedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getTransactionId(), copy.getTransactionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
