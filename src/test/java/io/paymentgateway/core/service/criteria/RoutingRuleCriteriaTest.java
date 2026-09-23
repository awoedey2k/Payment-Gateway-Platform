package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RoutingRuleCriteriaTest {

    @Test
    void newRoutingRuleCriteriaHasAllFiltersNullTest() {
        var routingRuleCriteria = new RoutingRuleCriteria();
        assertThat(routingRuleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void routingRuleCriteriaFluentMethodsCreatesFiltersTest() {
        var routingRuleCriteria = new RoutingRuleCriteria();

        setAllFilters(routingRuleCriteria);

        assertThat(routingRuleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void routingRuleCriteriaCopyCreatesNullFilterTest() {
        var routingRuleCriteria = new RoutingRuleCriteria();
        var copy = routingRuleCriteria.copy();

        assertThat(routingRuleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(routingRuleCriteria)
        );
    }

    @Test
    void routingRuleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var routingRuleCriteria = new RoutingRuleCriteria();
        setAllFilters(routingRuleCriteria);

        var copy = routingRuleCriteria.copy();

        assertThat(routingRuleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(routingRuleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var routingRuleCriteria = new RoutingRuleCriteria();

        assertThat(routingRuleCriteria).hasToString("RoutingRuleCriteria{}");
    }

    private static void setAllFilters(RoutingRuleCriteria routingRuleCriteria) {
        routingRuleCriteria.id();
        routingRuleCriteria.priority();
        routingRuleCriteria.scope();
        routingRuleCriteria.countryCode();
        routingRuleCriteria.currencyCode();
        routingRuleCriteria.cardBrand();
        routingRuleCriteria.primaryAdapter();
        routingRuleCriteria.fallbackAdapter();
        routingRuleCriteria.maxRetries();
        routingRuleCriteria.isActive();
        routingRuleCriteria.tenantId();
        routingRuleCriteria.distinct();
    }

    private static Condition<RoutingRuleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPriority()) &&
                condition.apply(criteria.getScope()) &&
                condition.apply(criteria.getCountryCode()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getCardBrand()) &&
                condition.apply(criteria.getPrimaryAdapter()) &&
                condition.apply(criteria.getFallbackAdapter()) &&
                condition.apply(criteria.getMaxRetries()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RoutingRuleCriteria> copyFiltersAre(RoutingRuleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPriority(), copy.getPriority()) &&
                condition.apply(criteria.getScope(), copy.getScope()) &&
                condition.apply(criteria.getCountryCode(), copy.getCountryCode()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getCardBrand(), copy.getCardBrand()) &&
                condition.apply(criteria.getPrimaryAdapter(), copy.getPrimaryAdapter()) &&
                condition.apply(criteria.getFallbackAdapter(), copy.getFallbackAdapter()) &&
                condition.apply(criteria.getMaxRetries(), copy.getMaxRetries()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
