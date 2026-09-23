package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CountryPaymentMethodDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CountryPaymentMethodDTO.class);
        CountryPaymentMethodDTO countryPaymentMethodDTO1 = new CountryPaymentMethodDTO();
        countryPaymentMethodDTO1.setId(1L);
        CountryPaymentMethodDTO countryPaymentMethodDTO2 = new CountryPaymentMethodDTO();
        assertThat(countryPaymentMethodDTO1).isNotEqualTo(countryPaymentMethodDTO2);
        countryPaymentMethodDTO2.setId(countryPaymentMethodDTO1.getId());
        assertThat(countryPaymentMethodDTO1).isEqualTo(countryPaymentMethodDTO2);
        countryPaymentMethodDTO2.setId(2L);
        assertThat(countryPaymentMethodDTO1).isNotEqualTo(countryPaymentMethodDTO2);
        countryPaymentMethodDTO1.setId(null);
        assertThat(countryPaymentMethodDTO1).isNotEqualTo(countryPaymentMethodDTO2);
    }
}
