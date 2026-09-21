package io.paymentgateway.core.extended.tenant.config;

import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyService;
import io.paymentgateway.core.extended.tenant.web.ApiKeyAuthenticationFilter;
import io.paymentgateway.core.extended.tenant.web.ApiKeyAuthenticationToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * Security for the merchant-facing API ({@code /api/v1/**}), authenticated by API key rather than by JHipster's staff JWT.
 *
 * <p>This is a second {@link SecurityFilterChain} with the highest precedence and a {@code /api/v1/**} matcher, so the
 * generated {@code SecurityConfiguration} (which handles everything else, including {@code /api/**} staff endpoints) is
 * neither edited nor consulted for these paths. {@code /api/v1/public/**} needs no credentials (checkout page config).
 */
@Configuration
public class TenantSecurityConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain merchantApiSecurityFilterChain(HttpSecurity http, ApiKeyService apiKeys) throws Exception {
        http.securityMatcher("/api/v1/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new ApiKeyAuthenticationFilter(apiKeys), AuthorizationFilter.class)
            .authorizeHttpRequests(authz ->
                authz.requestMatchers("/api/v1/public/**").permitAll().anyRequest().hasAuthority(ApiKeyAuthenticationToken.AUTHORITY)
            )
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint((request, response, e) ->
                        writeProblem(
                            response,
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "API_KEY_REQUIRED",
                            "Send your secret key as 'Authorization: Bearer sk_...'"
                        )
                    )
                    .accessDeniedHandler((request, response, e) ->
                        writeProblem(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Access denied")
                    )
            );
        return http.build();
    }

    private static void writeProblem(HttpServletResponse response, int status, String code, String detail) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/problem+json");
        response.getWriter().write("{\"status\":" + status + ",\"code\":\"" + code + "\",\"detail\":\"" + detail + "\"}");
    }
}
