package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WebhookSubscriptionCriteriaTest {

    @Test
    void newWebhookSubscriptionCriteriaHasAllFiltersNullTest() {
        var webhookSubscriptionCriteria = new WebhookSubscriptionCriteria();
        assertThat(webhookSubscriptionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void webhookSubscriptionCriteriaFluentMethodsCreatesFiltersTest() {
        var webhookSubscriptionCriteria = new WebhookSubscriptionCriteria();

        setAllFilters(webhookSubscriptionCriteria);

        assertThat(webhookSubscriptionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void webhookSubscriptionCriteriaCopyCreatesNullFilterTest() {
        var webhookSubscriptionCriteria = new WebhookSubscriptionCriteria();
        var copy = webhookSubscriptionCriteria.copy();

        assertThat(webhookSubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookSubscriptionCriteria)
        );
    }

    @Test
    void webhookSubscriptionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var webhookSubscriptionCriteria = new WebhookSubscriptionCriteria();
        setAllFilters(webhookSubscriptionCriteria);

        var copy = webhookSubscriptionCriteria.copy();

        assertThat(webhookSubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookSubscriptionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var webhookSubscriptionCriteria = new WebhookSubscriptionCriteria();

        assertThat(webhookSubscriptionCriteria).hasToString("WebhookSubscriptionCriteria{}");
    }

    private static void setAllFilters(WebhookSubscriptionCriteria webhookSubscriptionCriteria) {
        webhookSubscriptionCriteria.id();
        webhookSubscriptionCriteria.targetUrl();
        webhookSubscriptionCriteria.secretHash();
        webhookSubscriptionCriteria.isActive();
        webhookSubscriptionCriteria.tenantId();
        webhookSubscriptionCriteria.distinct();
    }

    private static Condition<WebhookSubscriptionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTargetUrl()) &&
                condition.apply(criteria.getSecretHash()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WebhookSubscriptionCriteria> copyFiltersAre(
        WebhookSubscriptionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTargetUrl(), copy.getTargetUrl()) &&
                condition.apply(criteria.getSecretHash(), copy.getSecretHash()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
