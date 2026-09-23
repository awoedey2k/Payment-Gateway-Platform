package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.CurrencyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CurrencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Currency.class);
        Currency currency1 = getCurrencySample1();
        Currency currency2 = new Currency();
        assertThat(currency1).isNotEqualTo(currency2);

        currency2.setId(currency1.getId());
        assertThat(currency1).isEqualTo(currency2);

        currency2 = getCurrencySample2();
        assertThat(currency1).isNotEqualTo(currency2);
    }
}
