package io.paymentgateway.core.extended.tenant.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Replaces the production {@code Clock} with a {@link MutableClock} in integration tests that import it. */
@TestConfiguration
public class TestClockConfiguration {

    @Bean
    @Primary
    public MutableClock testClock() {
        return new MutableClock();
    }
}
