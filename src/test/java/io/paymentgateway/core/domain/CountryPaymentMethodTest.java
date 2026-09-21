package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CountryPaymentMethodTestSamples.*;
import static io.paymentgateway.core.domain.CountryTestSamples.*;
import static io.paymentgateway.core.domain.PaymentMethodTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CountryPaymentMethodTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CountryPaymentMethod.class);
        CountryPaymentMethod countryPaymentMethod1 = getCountryPaymentMethodSample1();
        CountryPaymentMethod countryPaymentMethod2 = new CountryPaymentMethod();
        assertThat(countryPaymentMethod1).isNotEqualTo(countryPaymentMethod2);

        countryPaymentMethod2.setId(countryPaymentMethod1.getId());
        assertThat(countryPaymentMethod1).isEqualTo(countryPaymentMethod2);

        countryPaymentMethod2 = getCountryPaymentMethodSample2();
        assertThat(countryPaymentMethod1).isNotEqualTo(countryPaymentMethod2);
    }

    @Test
    void countryTest() {
        CountryPaymentMethod countryPaymentMethod = getCountryPaymentMethodRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        countryPaymentMethod.setCountry(countryBack);
        assertThat(countryPaymentMethod.getCountry()).isEqualTo(countryBack);

        countryPaymentMethod.country(null);
        assertThat(countryPaymentMethod.getCountry()).isNull();
    }

    @Test
    void paymentMethodTest() {
        CountryPaymentMethod countryPaymentMethod = getCountryPaymentMethodRandomSampleGenerator();
        PaymentMethod paymentMethodBack = getPaymentMethodRandomSampleGenerator();

        countryPaymentMethod.setPaymentMethod(paymentMethodBack);
        assertThat(countryPaymentMethod.getPaymentMethod()).isEqualTo(paymentMethodBack);

        countryPaymentMethod.paymentMethod(null);
        assertThat(countryPaymentMethod.getPaymentMethod()).isNull();
    }
}
