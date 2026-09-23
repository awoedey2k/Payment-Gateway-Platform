package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuditLogEntryCriteriaTest {

    @Test
    void newAuditLogEntryCriteriaHasAllFiltersNullTest() {
        var auditLogEntryCriteria = new AuditLogEntryCriteria();
        assertThat(auditLogEntryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void auditLogEntryCriteriaFluentMethodsCreatesFiltersTest() {
        var auditLogEntryCriteria = new AuditLogEntryCriteria();

        setAllFilters(auditLogEntryCriteria);

        assertThat(auditLogEntryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void auditLogEntryCriteriaCopyCreatesNullFilterTest() {
        var auditLogEntryCriteria = new AuditLogEntryCriteria();
        var copy = auditLogEntryCriteria.copy();

        assertThat(auditLogEntryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(auditLogEntryCriteria)
        );
    }

    @Test
    void auditLogEntryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var auditLogEntryCriteria = new AuditLogEntryCriteria();
        setAllFilters(auditLogEntryCriteria);

        var copy = auditLogEntryCriteria.copy();

        assertThat(auditLogEntryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(auditLogEntryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var auditLogEntryCriteria = new AuditLogEntryCriteria();

        assertThat(auditLogEntryCriteria).hasToString("AuditLogEntryCriteria{}");
    }

    private static void setAllFilters(AuditLogEntryCriteria auditLogEntryCriteria) {
        auditLogEntryCriteria.id();
        auditLogEntryCriteria.actorType();
        auditLogEntryCriteria.actorId();
        auditLogEntryCriteria.action();
        auditLogEntryCriteria.entityType();
        auditLogEntryCriteria.entityId();
        auditLogEntryCriteria.previousHash();
        auditLogEntryCriteria.entryHash();
        auditLogEntryCriteria.recordedAt();
        auditLogEntryCriteria.distinct();
    }

    private static Condition<AuditLogEntryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getActorType()) &&
                condition.apply(criteria.getActorId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getEntityType()) &&
                condition.apply(criteria.getEntityId()) &&
                condition.apply(criteria.getPreviousHash()) &&
                condition.apply(criteria.getEntryHash()) &&
                condition.apply(criteria.getRecordedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuditLogEntryCriteria> copyFiltersAre(
        AuditLogEntryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getActorType(), copy.getActorType()) &&
                condition.apply(criteria.getActorId(), copy.getActorId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getEntityType(), copy.getEntityType()) &&
                condition.apply(criteria.getEntityId(), copy.getEntityId()) &&
                condition.apply(criteria.getPreviousHash(), copy.getPreviousHash()) &&
                condition.apply(criteria.getEntryHash(), copy.getEntryHash()) &&
                condition.apply(criteria.getRecordedAt(), copy.getRecordedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
