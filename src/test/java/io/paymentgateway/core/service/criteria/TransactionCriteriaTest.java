package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TransactionCriteriaTest {

    @Test
    void newTransactionCriteriaHasAllFiltersNullTest() {
        var transactionCriteria = new TransactionCriteria();
        assertThat(transactionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void transactionCriteriaFluentMethodsCreatesFiltersTest() {
        var transactionCriteria = new TransactionCriteria();

        setAllFilters(transactionCriteria);

        assertThat(transactionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void transactionCriteriaCopyCreatesNullFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void transactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        setAllFilters(transactionCriteria);

        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var transactionCriteria = new TransactionCriteria();

        assertThat(transactionCriteria).hasToString("TransactionCriteria{}");
    }

    private static void setAllFilters(TransactionCriteria transactionCriteria) {
        transactionCriteria.id();
        transactionCriteria.reference();
        transactionCriteria.tenantReference();
        transactionCriteria.status();
        transactionCriteria.amount();
        transactionCriteria.feeAmount();
        transactionCriteria.netAmount();
        transactionCriteria.currencyCode();
        transactionCriteria.countryCode();
        transactionCriteria.paymentMethodCode();
        transactionCriteria.idempotencyKey();
        transactionCriteria.customerEmail();
        transactionCriteria.customerPhone();
        transactionCriteria.createdAt();
        transactionCriteria.completedAt();
        transactionCriteria.refundId();
        transactionCriteria.tenantId();
        transactionCriteria.distinct();
    }

    private static Condition<TransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTenantReference()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getFeeAmount()) &&
                condition.apply(criteria.getNetAmount()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getCountryCode()) &&
                condition.apply(criteria.getPaymentMethodCode()) &&
                condition.apply(criteria.getIdempotencyKey()) &&
                condition.apply(criteria.getCustomerEmail()) &&
                condition.apply(criteria.getCustomerPhone()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getRefundId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TransactionCriteria> copyFiltersAre(TransactionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTenantReference(), copy.getTenantReference()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getFeeAmount(), copy.getFeeAmount()) &&
                condition.apply(criteria.getNetAmount(), copy.getNetAmount()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getCountryCode(), copy.getCountryCode()) &&
                condition.apply(criteria.getPaymentMethodCode(), copy.getPaymentMethodCode()) &&
                condition.apply(criteria.getIdempotencyKey(), copy.getIdempotencyKey()) &&
                condition.apply(criteria.getCustomerEmail(), copy.getCustomerEmail()) &&
                condition.apply(criteria.getCustomerPhone(), copy.getCustomerPhone()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getRefundId(), copy.getRefundId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
