package io.paymentgateway.core.extended.tenant.web;

import io.paymentgateway.core.extended.tenant.domain.ApiKeyPrincipal;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** An already-verified merchant API key. Deliberately carries no credentials. */
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    public static final String AUTHORITY = "ROLE_MERCHANT_API";

    private final transient ApiKeyPrincipal principal;

    public ApiKeyAuthenticationToken(ApiKeyPrincipal principal) {
        super(List.of(new SimpleGrantedAuthority(AUTHORITY)));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public ApiKeyPrincipal getPrincipal() {
        return principal;
    }
}
