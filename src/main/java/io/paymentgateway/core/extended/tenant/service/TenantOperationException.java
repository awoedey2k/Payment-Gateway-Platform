package io.paymentgateway.core.extended.tenant.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * A rejected tenant operation with a stable machine-readable {@code code}. Extends {@link ErrorResponseException} so the
 * generated exception translator renders it as an RFC 9457 problem response with the right HTTP status and the {@code code}.
 */
public class TenantOperationException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public TenantOperationException(HttpStatus status, String code, String message) {
        super(status, problem(status, code, message), null);
        this.code = code;
    }

    /**
     * Built as JHipster's {@link ProblemDetailWithCause}: the generated exception translator only keeps the body of an
     * {@code ErrorResponseException} of that type, and would otherwise drop the {@code code}.
     */
    private static ProblemDetailWithCause problem(HttpStatus status, String code, String message) {
        return ProblemDetailWithCauseBuilder.instance()
            .withStatus(status.value())
            .withTitle(code)
            .withDetail(message)
            .withProperty("code", code)
            .withProperty("message", "error.tenant." + code.toLowerCase(java.util.Locale.ROOT))
            .build();
    }

    public String getCode() {
        return code;
    }

    public static TenantOperationException notFound(String what, Object id) {
        return new TenantOperationException(HttpStatus.NOT_FOUND, "NOT_FOUND", what + " " + id + " not found");
    }

    public static TenantOperationException conflict(String code, String message) {
        return new TenantOperationException(HttpStatus.CONFLICT, code, message);
    }

    public static TenantOperationException badRequest(String code, String message) {
        return new TenantOperationException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static TenantOperationException forbidden(String code, String message) {
        return new TenantOperationException(HttpStatus.FORBIDDEN, code, message);
    }
}
