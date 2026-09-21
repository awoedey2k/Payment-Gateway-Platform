package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.ForexRateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ForexRateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ForexRate.class);
        ForexRate forexRate1 = getForexRateSample1();
        ForexRate forexRate2 = new ForexRate();
        assertThat(forexRate1).isNotEqualTo(forexRate2);

        forexRate2.setId(forexRate1.getId());
        assertThat(forexRate1).isEqualTo(forexRate2);

        forexRate2 = getForexRateSample2();
        assertThat(forexRate1).isNotEqualTo(forexRate2);
    }
}
