package io.paymentgateway.core.extended.tenant.service.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.TenantDomain;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CheckoutDomainServiceTest {

    private static final List<String> SUPPORTED = List.of("en_US", "fr_SN", "pt_AO");

    private static TenantDomain domain(String defaultLocale, String fallbackLocale) {
        return new TenantDomain().customDomain("pay.swiftmarket.com").defaultLocale(defaultLocale).fallbackLocale(fallbackLocale);
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "pay.swiftmarket.com|pay.swiftmarket.com",
            "PAY.SwiftMarket.COM|pay.swiftmarket.com",
            "pay.swiftmarket.com:8443|pay.swiftmarket.com",
            "pay.swiftmarket.com.|pay.swiftmarket.com",
            "  pay.swiftmarket.com  |pay.swiftmarket.com",
        }
    )
    void hostIsNormalisedBeforeLookup(String header, String expected) {
        assertThat(CheckoutDomainService.normaliseHost(header)).isEqualTo(expected);
    }

    @Test
    void missingHostNormalisesToEmpty() {
        assertThat(CheckoutDomainService.normaliseHost(null)).isEmpty();
        assertThat(CheckoutDomainService.normaliseHost("  ")).isEmpty();
    }

    @Test
    void anExactAcceptLanguageMatchWins() {
        assertThat(CheckoutDomainService.negotiate(domain("en_US", "en_US"), SUPPORTED, "fr-SN")).isEqualTo("fr_SN");
        assertThat(CheckoutDomainService.negotiate(domain("en_US", "en_US"), SUPPORTED, "de;q=0.9, pt-AO;q=0.8")).isEqualTo("pt_AO");
    }

    @Test
    void qualityValuesDecideBetweenSupportedLocales() {
        assertThat(CheckoutDomainService.negotiate(domain("en_US", null), SUPPORTED, "pt-AO;q=0.4, fr-SN;q=0.9")).isEqualTo("fr_SN");
    }

    @Test
    void aDifferentRegionOfASupportedLanguageStillMatchesTheLanguage() {
        assertThat(CheckoutDomainService.negotiate(domain("en_US", null), SUPPORTED, "fr-FR,fr;q=0.9")).isEqualTo("fr_SN");
    }

    @Test
    void noMatchFallsBackToDefaultThenFallbackThenGlobal() {
        assertThat(CheckoutDomainService.negotiate(domain("pt_AO", "en_US"), SUPPORTED, "ja-JP")).isEqualTo("pt_AO");
        assertThat(CheckoutDomainService.negotiate(domain(null, "fr_SN"), SUPPORTED, "ja-JP")).isEqualTo("fr_SN");
        assertThat(CheckoutDomainService.negotiate(domain(null, null), SUPPORTED, "ja-JP")).isEqualTo("en_US");
        assertThat(CheckoutDomainService.negotiate(domain(" ", ""), SUPPORTED, null)).isEqualTo("en_US");
    }

    @Test
    void aMalformedAcceptLanguageHeaderIsIgnoredNotAnError() {
        assertThat(CheckoutDomainService.negotiate(domain("pt_AO", null), SUPPORTED, "!!!;;;q=abc")).isEqualTo("pt_AO");
    }

    @Test
    void noSupportedLocalesMeansDefaultsApply() {
        assertThat(CheckoutDomainService.negotiate(domain("fr_SN", null), List.of(), "fr-SN")).isEqualTo("fr_SN");
    }
}
