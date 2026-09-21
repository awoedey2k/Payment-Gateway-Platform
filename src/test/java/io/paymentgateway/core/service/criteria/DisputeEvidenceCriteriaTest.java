package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DisputeEvidenceCriteriaTest {

    @Test
    void newDisputeEvidenceCriteriaHasAllFiltersNullTest() {
        var disputeEvidenceCriteria = new DisputeEvidenceCriteria();
        assertThat(disputeEvidenceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void disputeEvidenceCriteriaFluentMethodsCreatesFiltersTest() {
        var disputeEvidenceCriteria = new DisputeEvidenceCriteria();

        setAllFilters(disputeEvidenceCriteria);

        assertThat(disputeEvidenceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void disputeEvidenceCriteriaCopyCreatesNullFilterTest() {
        var disputeEvidenceCriteria = new DisputeEvidenceCriteria();
        var copy = disputeEvidenceCriteria.copy();

        assertThat(disputeEvidenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(disputeEvidenceCriteria)
        );
    }

    @Test
    void disputeEvidenceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var disputeEvidenceCriteria = new DisputeEvidenceCriteria();
        setAllFilters(disputeEvidenceCriteria);

        var copy = disputeEvidenceCriteria.copy();

        assertThat(disputeEvidenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(disputeEvidenceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var disputeEvidenceCriteria = new DisputeEvidenceCriteria();

        assertThat(disputeEvidenceCriteria).hasToString("DisputeEvidenceCriteria{}");
    }

    private static void setAllFilters(DisputeEvidenceCriteria disputeEvidenceCriteria) {
        disputeEvidenceCriteria.id();
        disputeEvidenceCriteria.evidenceType();
        disputeEvidenceCriteria.fileName();
        disputeEvidenceCriteria.fileUrl();
        disputeEvidenceCriteria.fileSizeBytes();
        disputeEvidenceCriteria.mimeType();
        disputeEvidenceCriteria.sha256Checksum();
        disputeEvidenceCriteria.uploadedAt();
        disputeEvidenceCriteria.disputeId();
        disputeEvidenceCriteria.distinct();
    }

    private static Condition<DisputeEvidenceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEvidenceType()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes()) &&
                condition.apply(criteria.getMimeType()) &&
                condition.apply(criteria.getSha256Checksum()) &&
                condition.apply(criteria.getUploadedAt()) &&
                condition.apply(criteria.getDisputeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DisputeEvidenceCriteria> copyFiltersAre(
        DisputeEvidenceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEvidenceType(), copy.getEvidenceType()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getFileUrl(), copy.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes(), copy.getFileSizeBytes()) &&
                condition.apply(criteria.getMimeType(), copy.getMimeType()) &&
                condition.apply(criteria.getSha256Checksum(), copy.getSha256Checksum()) &&
                condition.apply(criteria.getUploadedAt(), copy.getUploadedAt()) &&
                condition.apply(criteria.getDisputeId(), copy.getDisputeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
