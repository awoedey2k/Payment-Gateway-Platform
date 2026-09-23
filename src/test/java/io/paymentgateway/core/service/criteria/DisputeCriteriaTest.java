package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DisputeCriteriaTest {

    @Test
    void newDisputeCriteriaHasAllFiltersNullTest() {
        var disputeCriteria = new DisputeCriteria();
        assertThat(disputeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void disputeCriteriaFluentMethodsCreatesFiltersTest() {
        var disputeCriteria = new DisputeCriteria();

        setAllFilters(disputeCriteria);

        assertThat(disputeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void disputeCriteriaCopyCreatesNullFilterTest() {
        var disputeCriteria = new DisputeCriteria();
        var copy = disputeCriteria.copy();

        assertThat(disputeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(disputeCriteria)
        );
    }

    @Test
    void disputeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var disputeCriteria = new DisputeCriteria();
        setAllFilters(disputeCriteria);

        var copy = disputeCriteria.copy();

        assertThat(disputeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(disputeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var disputeCriteria = new DisputeCriteria();

        assertThat(disputeCriteria).hasToString("DisputeCriteria{}");
    }

    private static void setAllFilters(DisputeCriteria disputeCriteria) {
        disputeCriteria.id();
        disputeCriteria.caseReference();
        disputeCriteria.amount();
        disputeCriteria.currencyCode();
        disputeCriteria.reasonCode();
        disputeCriteria.reasonDescription();
        disputeCriteria.status();
        disputeCriteria.dueDate();
        disputeCriteria.evidenceSubmittedAt();
        disputeCriteria.resolvedAt();
        disputeCriteria.tenantId();
        disputeCriteria.transactionId();
        disputeCriteria.distinct();
    }

    private static Condition<DisputeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCaseReference()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getReasonCode()) &&
                condition.apply(criteria.getReasonDescription()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getDueDate()) &&
                condition.apply(criteria.getEvidenceSubmittedAt()) &&
                condition.apply(criteria.getResolvedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getTransactionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DisputeCriteria> copyFiltersAre(DisputeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCaseReference(), copy.getCaseReference()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getReasonCode(), copy.getReasonCode()) &&
                condition.apply(criteria.getReasonDescription(), copy.getReasonDescription()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getDueDate(), copy.getDueDate()) &&
                condition.apply(criteria.getEvidenceSubmittedAt(), copy.getEvidenceSubmittedAt()) &&
                condition.apply(criteria.getResolvedAt(), copy.getResolvedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getTransactionId(), copy.getTransactionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
