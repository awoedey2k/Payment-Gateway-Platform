package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ApiKeyCriteriaTest {

    @Test
    void newApiKeyCriteriaHasAllFiltersNullTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        assertThat(apiKeyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void apiKeyCriteriaFluentMethodsCreatesFiltersTest() {
        var apiKeyCriteria = new ApiKeyCriteria();

        setAllFilters(apiKeyCriteria);

        assertThat(apiKeyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void apiKeyCriteriaCopyCreatesNullFilterTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        var copy = apiKeyCriteria.copy();

        assertThat(apiKeyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(apiKeyCriteria)
        );
    }

    @Test
    void apiKeyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        setAllFilters(apiKeyCriteria);

        var copy = apiKeyCriteria.copy();

        assertThat(apiKeyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(apiKeyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var apiKeyCriteria = new ApiKeyCriteria();

        assertThat(apiKeyCriteria).hasToString("ApiKeyCriteria{}");
    }

    private static void setAllFilters(ApiKeyCriteria apiKeyCriteria) {
        apiKeyCriteria.id();
        apiKeyCriteria.keyPrefix();
        apiKeyCriteria.keyHash();
        apiKeyCriteria.environment();
        apiKeyCriteria.isActive();
        apiKeyCriteria.issuedAt();
        apiKeyCriteria.revokedAt();
        apiKeyCriteria.graceExpiresAt();
        apiKeyCriteria.tenantId();
        apiKeyCriteria.distinct();
    }

    private static Condition<ApiKeyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getKeyPrefix()) &&
                condition.apply(criteria.getKeyHash()) &&
                condition.apply(criteria.getEnvironment()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getIssuedAt()) &&
                condition.apply(criteria.getRevokedAt()) &&
                condition.apply(criteria.getGraceExpiresAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ApiKeyCriteria> copyFiltersAre(ApiKeyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getKeyPrefix(), copy.getKeyPrefix()) &&
                condition.apply(criteria.getKeyHash(), copy.getKeyHash()) &&
                condition.apply(criteria.getEnvironment(), copy.getEnvironment()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getIssuedAt(), copy.getIssuedAt()) &&
                condition.apply(criteria.getRevokedAt(), copy.getRevokedAt()) &&
                condition.apply(criteria.getGraceExpiresAt(), copy.getGraceExpiresAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
