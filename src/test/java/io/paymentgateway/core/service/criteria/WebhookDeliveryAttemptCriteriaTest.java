package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WebhookDeliveryAttemptCriteriaTest {

    @Test
    void newWebhookDeliveryAttemptCriteriaHasAllFiltersNullTest() {
        var webhookDeliveryAttemptCriteria = new WebhookDeliveryAttemptCriteria();
        assertThat(webhookDeliveryAttemptCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void webhookDeliveryAttemptCriteriaFluentMethodsCreatesFiltersTest() {
        var webhookDeliveryAttemptCriteria = new WebhookDeliveryAttemptCriteria();

        setAllFilters(webhookDeliveryAttemptCriteria);

        assertThat(webhookDeliveryAttemptCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void webhookDeliveryAttemptCriteriaCopyCreatesNullFilterTest() {
        var webhookDeliveryAttemptCriteria = new WebhookDeliveryAttemptCriteria();
        var copy = webhookDeliveryAttemptCriteria.copy();

        assertThat(webhookDeliveryAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookDeliveryAttemptCriteria)
        );
    }

    @Test
    void webhookDeliveryAttemptCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var webhookDeliveryAttemptCriteria = new WebhookDeliveryAttemptCriteria();
        setAllFilters(webhookDeliveryAttemptCriteria);

        var copy = webhookDeliveryAttemptCriteria.copy();

        assertThat(webhookDeliveryAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookDeliveryAttemptCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var webhookDeliveryAttemptCriteria = new WebhookDeliveryAttemptCriteria();

        assertThat(webhookDeliveryAttemptCriteria).hasToString("WebhookDeliveryAttemptCriteria{}");
    }

    private static void setAllFilters(WebhookDeliveryAttemptCriteria webhookDeliveryAttemptCriteria) {
        webhookDeliveryAttemptCriteria.id();
        webhookDeliveryAttemptCriteria.eventType();
        webhookDeliveryAttemptCriteria.status();
        webhookDeliveryAttemptCriteria.httpStatusCode();
        webhookDeliveryAttemptCriteria.attemptNumber();
        webhookDeliveryAttemptCriteria.attemptedAt();
        webhookDeliveryAttemptCriteria.subscriptionId();
        webhookDeliveryAttemptCriteria.distinct();
    }

    private static Condition<WebhookDeliveryAttemptCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEventType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getHttpStatusCode()) &&
                condition.apply(criteria.getAttemptNumber()) &&
                condition.apply(criteria.getAttemptedAt()) &&
                condition.apply(criteria.getSubscriptionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WebhookDeliveryAttemptCriteria> copyFiltersAre(
        WebhookDeliveryAttemptCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEventType(), copy.getEventType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getHttpStatusCode(), copy.getHttpStatusCode()) &&
                condition.apply(criteria.getAttemptNumber(), copy.getAttemptNumber()) &&
                condition.apply(criteria.getAttemptedAt(), copy.getAttemptedAt()) &&
                condition.apply(criteria.getSubscriptionId(), copy.getSubscriptionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
