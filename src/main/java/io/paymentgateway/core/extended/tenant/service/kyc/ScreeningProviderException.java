package io.paymentgateway.core.extended.tenant.service.kyc;

/** A screening or registry provider could not answer. Treated as a risk signal, never as "clear". */
public class ScreeningProviderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScreeningProviderException(String message) {
        super(message);
    }

    public ScreeningProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
