package io.paymentgateway.core.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CorporateTenantCriteriaTest {

    @Test
    void newCorporateTenantCriteriaHasAllFiltersNullTest() {
        var corporateTenantCriteria = new CorporateTenantCriteria();
        assertThat(corporateTenantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void corporateTenantCriteriaFluentMethodsCreatesFiltersTest() {
        var corporateTenantCriteria = new CorporateTenantCriteria();

        setAllFilters(corporateTenantCriteria);

        assertThat(corporateTenantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void corporateTenantCriteriaCopyCreatesNullFilterTest() {
        var corporateTenantCriteria = new CorporateTenantCriteria();
        var copy = corporateTenantCriteria.copy();

        assertThat(corporateTenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(corporateTenantCriteria)
        );
    }

    @Test
    void corporateTenantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var corporateTenantCriteria = new CorporateTenantCriteria();
        setAllFilters(corporateTenantCriteria);

        var copy = corporateTenantCriteria.copy();

        assertThat(corporateTenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(corporateTenantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var corporateTenantCriteria = new CorporateTenantCriteria();

        assertThat(corporateTenantCriteria).hasToString("CorporateTenantCriteria{}");
    }

    private static void setAllFilters(CorporateTenantCriteria corporateTenantCriteria) {
        corporateTenantCriteria.id();
        corporateTenantCriteria.legalBusinessName();
        corporateTenantCriteria.businessRegistrationNumber();
        corporateTenantCriteria.taxIdentificationNumber();
        corporateTenantCriteria.operatingJurisdiction();
        corporateTenantCriteria.status();
        corporateTenantCriteria.kycStatus();
        corporateTenantCriteria.riskScore();
        corporateTenantCriteria.createdAt();
        corporateTenantCriteria.activatedAt();
        corporateTenantCriteria.tenantDirectorId();
        corporateTenantCriteria.apiKeyId();
        corporateTenantCriteria.tenantDomainId();
        corporateTenantCriteria.distinct();
    }

    private static Condition<CorporateTenantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLegalBusinessName()) &&
                condition.apply(criteria.getBusinessRegistrationNumber()) &&
                condition.apply(criteria.getTaxIdentificationNumber()) &&
                condition.apply(criteria.getOperatingJurisdiction()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getKycStatus()) &&
                condition.apply(criteria.getRiskScore()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getActivatedAt()) &&
                condition.apply(criteria.getTenantDirectorId()) &&
                condition.apply(criteria.getApiKeyId()) &&
                condition.apply(criteria.getTenantDomainId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CorporateTenantCriteria> copyFiltersAre(
        CorporateTenantCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLegalBusinessName(), copy.getLegalBusinessName()) &&
                condition.apply(criteria.getBusinessRegistrationNumber(), copy.getBusinessRegistrationNumber()) &&
                condition.apply(criteria.getTaxIdentificationNumber(), copy.getTaxIdentificationNumber()) &&
                condition.apply(criteria.getOperatingJurisdiction(), copy.getOperatingJurisdiction()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getKycStatus(), copy.getKycStatus()) &&
                condition.apply(criteria.getRiskScore(), copy.getRiskScore()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getActivatedAt(), copy.getActivatedAt()) &&
                condition.apply(criteria.getTenantDirectorId(), copy.getTenantDirectorId()) &&
                condition.apply(criteria.getApiKeyId(), copy.getApiKeyId()) &&
                condition.apply(criteria.getTenantDomainId(), copy.getTenantDomainId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
