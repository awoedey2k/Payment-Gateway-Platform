package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantFeeConfigCriteriaTest {

    @Test
    void newTenantFeeConfigCriteriaHasAllFiltersNullTest() {
        var tenantFeeConfigCriteria = new TenantFeeConfigCriteria();
        assertThat(tenantFeeConfigCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantFeeConfigCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantFeeConfigCriteria = new TenantFeeConfigCriteria();

        setAllFilters(tenantFeeConfigCriteria);

        assertThat(tenantFeeConfigCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantFeeConfigCriteriaCopyCreatesNullFilterTest() {
        var tenantFeeConfigCriteria = new TenantFeeConfigCriteria();
        var copy = tenantFeeConfigCriteria.copy();

        assertThat(tenantFeeConfigCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantFeeConfigCriteria)
        );
    }

    @Test
    void tenantFeeConfigCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantFeeConfigCriteria = new TenantFeeConfigCriteria();
        setAllFilters(tenantFeeConfigCriteria);

        var copy = tenantFeeConfigCriteria.copy();

        assertThat(tenantFeeConfigCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantFeeConfigCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantFeeConfigCriteria = new TenantFeeConfigCriteria();

        assertThat(tenantFeeConfigCriteria).hasToString("TenantFeeConfigCriteria{}");
    }

    private static void setAllFilters(TenantFeeConfigCriteria tenantFeeConfigCriteria) {
        tenantFeeConfigCriteria.id();
        tenantFeeConfigCriteria.fixedFee();
        tenantFeeConfigCriteria.percentageFee();
        tenantFeeConfigCriteria.capAmount();
        tenantFeeConfigCriteria.feeBearer();
        tenantFeeConfigCriteria.isActive();
        tenantFeeConfigCriteria.tenantId();
        tenantFeeConfigCriteria.countryPaymentMethodId();
        tenantFeeConfigCriteria.distinct();
    }

    private static Condition<TenantFeeConfigCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFixedFee()) &&
                condition.apply(criteria.getPercentageFee()) &&
                condition.apply(criteria.getCapAmount()) &&
                condition.apply(criteria.getFeeBearer()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getCountryPaymentMethodId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantFeeConfigCriteria> copyFiltersAre(
        TenantFeeConfigCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFixedFee(), copy.getFixedFee()) &&
                condition.apply(criteria.getPercentageFee(), copy.getPercentageFee()) &&
                condition.apply(criteria.getCapAmount(), copy.getCapAmount()) &&
                condition.apply(criteria.getFeeBearer(), copy.getFeeBearer()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getCountryPaymentMethodId(), copy.getCountryPaymentMethodId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
