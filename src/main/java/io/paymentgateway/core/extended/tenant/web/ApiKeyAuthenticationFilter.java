package io.paymentgateway.core.extended.tenant.web;

import io.paymentgateway.core.extended.tenant.domain.ApiKeyAuthResult;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates merchant requests carrying {@code Authorization: Bearer sk_live_...} / {@code sk_test_...}.
 *
 * <ul>
 *   <li>Unknown, malformed, revoked, inactive or grace-expired key: HTTP 401 {@code INVALID_API_KEY}.
 *   <li>Valid key but the tenant may not process requests (not verified, suspended, ...): HTTP 403 with the tenant error code.
 *   <li>No {@code Authorization} header: the request continues unauthenticated and the security chain decides.
 * </ul>
 *
 * Created by {@link io.paymentgateway.core.extended.tenant.config.TenantSecurityConfiguration}; it is intentionally not a
 * Spring bean so it is not also registered as a servlet filter for every URL.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final String PUBLIC_PREFIX = "/api/v1/public/";

    private final ApiKeyService apiKeys;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith(PUBLIC_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER)) {
            chain.doFilter(request, response);
            return;
        }
        Optional<ApiKeyAuthResult> result = apiKeys.authenticate(header.substring(BEARER.length()).trim());
        if (result.isEmpty()) {
            ProblemJson.write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "INVALID_API_KEY",
                "The API key is invalid, revoked or expired"
            );
            return;
        }
        ApiKeyAuthResult authenticated = result.orElseThrow();
        if (!authenticated.decision().isAllowed()) {
            ProblemJson.write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                authenticated.decision().errorCode(),
                "This tenant may not process requests in its current state"
            );
            return;
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new ApiKeyAuthenticationToken(authenticated.principal()));
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
