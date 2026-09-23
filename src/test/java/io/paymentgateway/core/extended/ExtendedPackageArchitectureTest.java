package io.paymentgateway.core.extended;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.paymentgateway.core.PaymentgatewayApp;

/**
 * Mechanical checks for the project rule that all hand-written code lives in {@code io.paymentgateway.core.extended}
 * and that JHipster-generated code is never edited to know about it.
 */
@AnalyzeClasses(packagesOf = PaymentgatewayApp.class, importOptions = DoNotIncludeTests.class)
class ExtendedPackageArchitectureTest {

    private static final String EXTENDED = "io.paymentgateway.core.extended..";

    /** Generated code must not reference hand-written code: the dependency only ever points from extended to generated. */
    @ArchTest
    static final ArchRule generatedCodeDoesNotDependOnExtendedCode = noClasses()
        .that()
        .resideOutsideOfPackage(EXTENDED)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(EXTENDED);

    /** Modules talk through each other's service packages, never through each other's repositories (spec §2.1). */
    @ArchTest
    static final ArchRule tenantRepositoriesAreNotUsedByOtherModules = noClasses()
        .that()
        .resideOutsideOfPackages("io.paymentgateway.core.extended.tenant..")
        .and()
        .resideInAPackage(EXTENDED)
        .should()
        .dependOnClassesThat()
        .resideInAPackage("io.paymentgateway.core.extended.tenant.repository..");
}
