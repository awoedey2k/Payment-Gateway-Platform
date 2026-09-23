package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantDirectorCriteriaTest {

    @Test
    void newTenantDirectorCriteriaHasAllFiltersNullTest() {
        var tenantDirectorCriteria = new TenantDirectorCriteria();
        assertThat(tenantDirectorCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantDirectorCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantDirectorCriteria = new TenantDirectorCriteria();

        setAllFilters(tenantDirectorCriteria);

        assertThat(tenantDirectorCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantDirectorCriteriaCopyCreatesNullFilterTest() {
        var tenantDirectorCriteria = new TenantDirectorCriteria();
        var copy = tenantDirectorCriteria.copy();

        assertThat(tenantDirectorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantDirectorCriteria)
        );
    }

    @Test
    void tenantDirectorCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantDirectorCriteria = new TenantDirectorCriteria();
        setAllFilters(tenantDirectorCriteria);

        var copy = tenantDirectorCriteria.copy();

        assertThat(tenantDirectorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantDirectorCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantDirectorCriteria = new TenantDirectorCriteria();

        assertThat(tenantDirectorCriteria).hasToString("TenantDirectorCriteria{}");
    }

    private static void setAllFilters(TenantDirectorCriteria tenantDirectorCriteria) {
        tenantDirectorCriteria.id();
        tenantDirectorCriteria.fullName();
        tenantDirectorCriteria.dateOfBirth();
        tenantDirectorCriteria.nationality();
        tenantDirectorCriteria.identificationType();
        tenantDirectorCriteria.identificationNumber();
        tenantDirectorCriteria.tenantId();
        tenantDirectorCriteria.distinct();
    }

    private static Condition<TenantDirectorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getDateOfBirth()) &&
                condition.apply(criteria.getNationality()) &&
                condition.apply(criteria.getIdentificationType()) &&
                condition.apply(criteria.getIdentificationNumber()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantDirectorCriteria> copyFiltersAre(
        TenantDirectorCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getDateOfBirth(), copy.getDateOfBirth()) &&
                condition.apply(criteria.getNationality(), copy.getNationality()) &&
                condition.apply(criteria.getIdentificationType(), copy.getIdentificationType()) &&
                condition.apply(criteria.getIdentificationNumber(), copy.getIdentificationNumber()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
