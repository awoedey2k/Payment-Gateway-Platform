package io.paymentgateway.core.extended.common.config;

import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Decides whether the admin-only lockdown of the generated CRUD endpoints is active.
 *
 * <p>Explicit property {@code payment-gateway.security.restrict-generated-crud} wins. Without it the lockdown is <b>on</b>
 * unless a {@code test*} profile is active: the generated integration tests call the generated endpoints as a plain
 * {@code ROLE_USER} and cannot be edited, so they run against the open endpoints, while every real runtime (dev, prod,
 * anything else, and a run with no profile) is locked down. Tests of the lockdown itself switch it on with the property.
 */
public class GeneratedCrudLockdownCondition implements Condition {

    public static final String PROPERTY = "payment-gateway.security.restrict-generated-crud";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return isEnabled(context.getEnvironment());
    }

    static boolean isEnabled(Environment environment) {
        String explicit = environment.getProperty(PROPERTY);
        if (explicit != null) {
            return Boolean.parseBoolean(explicit.trim());
        }
        return Arrays.stream(environment.getActiveProfiles()).noneMatch(profile -> profile.startsWith("test"));
    }
}
