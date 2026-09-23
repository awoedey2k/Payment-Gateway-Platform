package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantDomainCriteriaTest {

    @Test
    void newTenantDomainCriteriaHasAllFiltersNullTest() {
        var tenantDomainCriteria = new TenantDomainCriteria();
        assertThat(tenantDomainCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantDomainCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantDomainCriteria = new TenantDomainCriteria();

        setAllFilters(tenantDomainCriteria);

        assertThat(tenantDomainCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantDomainCriteriaCopyCreatesNullFilterTest() {
        var tenantDomainCriteria = new TenantDomainCriteria();
        var copy = tenantDomainCriteria.copy();

        assertThat(tenantDomainCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantDomainCriteria)
        );
    }

    @Test
    void tenantDomainCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantDomainCriteria = new TenantDomainCriteria();
        setAllFilters(tenantDomainCriteria);

        var copy = tenantDomainCriteria.copy();

        assertThat(tenantDomainCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantDomainCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantDomainCriteria = new TenantDomainCriteria();

        assertThat(tenantDomainCriteria).hasToString("TenantDomainCriteria{}");
    }

    private static void setAllFilters(TenantDomainCriteria tenantDomainCriteria) {
        tenantDomainCriteria.id();
        tenantDomainCriteria.customDomain();
        tenantDomainCriteria.supportedLocales();
        tenantDomainCriteria.defaultLocale();
        tenantDomainCriteria.fallbackLocale();
        tenantDomainCriteria.isVerified();
        tenantDomainCriteria.tenantId();
        tenantDomainCriteria.distinct();
    }

    private static Condition<TenantDomainCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCustomDomain()) &&
                condition.apply(criteria.getSupportedLocales()) &&
                condition.apply(criteria.getDefaultLocale()) &&
                condition.apply(criteria.getFallbackLocale()) &&
                condition.apply(criteria.getIsVerified()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantDomainCriteria> copyFiltersAre(
        TenantDomainCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCustomDomain(), copy.getCustomDomain()) &&
                condition.apply(criteria.getSupportedLocales(), copy.getSupportedLocales()) &&
                condition.apply(criteria.getDefaultLocale(), copy.getDefaultLocale()) &&
                condition.apply(criteria.getFallbackLocale(), copy.getFallbackLocale()) &&
                condition.apply(criteria.getIsVerified(), copy.getIsVerified()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
