package io.paymentgateway.core.extended.tenant.support;

import io.paymentgateway.core.extended.tenant.properties.TenantProperties;
import java.util.Map;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

/** Builds {@link TenantProperties} the way Spring does, so tests see the real {@code @DefaultValue}s. */
public final class TestTenantProperties {

    private TestTenantProperties() {}

    public static TenantProperties defaults() {
        return with(Map.of());
    }

    public static TenantProperties with(Map<String, String> overrides) {
        return new Binder(new MapConfigurationPropertySource(overrides)).bindOrCreate("payment-gateway.tenant", TenantProperties.class);
    }
}
