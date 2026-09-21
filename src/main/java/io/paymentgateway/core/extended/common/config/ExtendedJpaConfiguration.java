package io.paymentgateway.core.extended.common.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Wiring for hand-written code that JHipster's generated configuration does not cover.
 *
 * <p>The generated {@code DatabaseConfiguration} only scans {@code io.paymentgateway.core.repository}, so repositories
 * that live in the {@code extended} package need their own scan. It is registered here rather than by editing the
 * generated class.
 */
@Configuration
@EnableJpaRepositories(basePackages = "io.paymentgateway.core.extended")
public class ExtendedJpaConfiguration {

    /** Single time source for hand-written code, so time-dependent behaviour (grace periods, expiry) is testable; tests override it with a {@code @Primary} clock. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
