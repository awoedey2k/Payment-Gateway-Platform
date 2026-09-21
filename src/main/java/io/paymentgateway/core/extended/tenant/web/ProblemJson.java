package io.paymentgateway.core.extended.tenant.web;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Writes the small fixed error bodies produced inside security filters, where message converters are not available. */
public final class ProblemJson {

    private ProblemJson() {}

    /** {@code code} and {@code detail} must be constants (never user input): they are written without escaping. */
    public static void write(HttpServletResponse response, int status, String code, String detail) throws IOException {
        response.setStatus(status);
        if (status == HttpServletResponse.SC_UNAUTHORIZED) {
            response.setHeader("WWW-Authenticate", "Bearer realm=\"payment-gateway\"");
        }
        response.setContentType("application/problem+json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status + ",\"code\":\"" + code + "\",\"detail\":\"" + detail + "\"}");
    }
}
