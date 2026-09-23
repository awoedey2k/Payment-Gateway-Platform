package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantWalletCriteriaTest {

    @Test
    void newTenantWalletCriteriaHasAllFiltersNullTest() {
        var tenantWalletCriteria = new TenantWalletCriteria();
        assertThat(tenantWalletCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantWalletCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantWalletCriteria = new TenantWalletCriteria();

        setAllFilters(tenantWalletCriteria);

        assertThat(tenantWalletCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantWalletCriteriaCopyCreatesNullFilterTest() {
        var tenantWalletCriteria = new TenantWalletCriteria();
        var copy = tenantWalletCriteria.copy();

        assertThat(tenantWalletCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantWalletCriteria)
        );
    }

    @Test
    void tenantWalletCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantWalletCriteria = new TenantWalletCriteria();
        setAllFilters(tenantWalletCriteria);

        var copy = tenantWalletCriteria.copy();

        assertThat(tenantWalletCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantWalletCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantWalletCriteria = new TenantWalletCriteria();

        assertThat(tenantWalletCriteria).hasToString("TenantWalletCriteria{}");
    }

    private static void setAllFilters(TenantWalletCriteria tenantWalletCriteria) {
        tenantWalletCriteria.id();
        tenantWalletCriteria.currencyCode();
        tenantWalletCriteria.availableBalance();
        tenantWalletCriteria.lockedBalance();
        tenantWalletCriteria.tenantId();
        tenantWalletCriteria.distinct();
    }

    private static Condition<TenantWalletCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getAvailableBalance()) &&
                condition.apply(criteria.getLockedBalance()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantWalletCriteria> copyFiltersAre(
        TenantWalletCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getAvailableBalance(), copy.getAvailableBalance()) &&
                condition.apply(criteria.getLockedBalance(), copy.getLockedBalance()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
