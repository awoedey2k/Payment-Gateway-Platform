package io.paymentgateway.core.extended.common.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.paymentgateway.core.security.AuthoritiesConstants;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Restricts the JHipster-generated entity CRUD endpoints ({@code /api/corporate-tenants}, {@code /api/api-keys},
 * {@code /api/transactions}, ...) to {@code ROLE_ADMIN}.
 *
 * <p>Why: the generated {@code SecurityConfiguration} protects {@code /api/**} with {@code authenticated()} only, and
 * {@code /api/register} is open, so any self-registered user could otherwise edit a tenant's status or KYC result, reactivate
 * a revoked API key, or write ledger rows directly, bypassing every guard implemented in {@code extended}. The generated
 * class cannot be edited, so this second chain (higher precedence than the generated one) takes those requests first.
 *
 * <p>It matches {@code /api/**} <em>except</em> a fixed list of paths that are not entity CRUD; a newly generated entity is
 * therefore locked down by default (fail closed). Active per {@link GeneratedCrudLockdownCondition}.
 */
@Configuration
@Conditional(GeneratedCrudLockdownCondition.class)
public class GeneratedCrudLockdownConfiguration {

    /** Non-entity API paths, left to the generated chain (and, for /api/v1, the merchant API-key chain). */
    static final List<String> NOT_ENTITY_CRUD = List.of(
        "/api/authenticate",
        "/api/register",
        "/api/activate",
        "/api/account/**",
        "/api/admin/**",
        "/api/extended/**",
        "/api/v1/**",
        "/api/users"
    );

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain generatedCrudLockdownFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(entityCrudRequests())
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz.anyRequest().hasAuthority(AuthoritiesConstants.ADMIN))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            // Same JWT decoder and authorities mapping as the generated chain (both come from Spring Boot/JHipster beans).
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    static RequestMatcher entityCrudRequests() {
        PathPatternRequestMatcher.Builder paths = PathPatternRequestMatcher.withDefaults();
        RequestMatcher anyApi = paths.matcher("/api/**");
        RequestMatcher excluded = new OrRequestMatcher(NOT_ENTITY_CRUD.stream().map(paths::matcher).toList());
        return request -> anyApi.matches(request) && !excluded.matches(request);
    }
}
