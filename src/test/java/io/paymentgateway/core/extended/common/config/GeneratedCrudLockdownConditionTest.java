package io.paymentgateway.core.extended.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class GeneratedCrudLockdownConditionTest {

    private static MockEnvironment env(String... profiles) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(profiles);
        return env;
    }

    @Test
    void everyRealRuntimeIsLockedDown() {
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env("dev"))).isTrue();
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env("prod"))).isTrue();
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env("staging", "cloud"))).isTrue();
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env())).isTrue(); // no profile at all: locked
    }

    @Test
    void generatedTestProfilesAreLeftOpenSoTheUneditableGeneratedTestsKeepWorking() {
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env("testdev"))).isFalse();
        assertThat(GeneratedCrudLockdownCondition.isEnabled(env("testprod"))).isFalse();
    }

    @Test
    void anExplicitPropertyOverridesTheProfileRule() {
        assertThat(
            GeneratedCrudLockdownCondition.isEnabled(env("testdev").withProperty(GeneratedCrudLockdownCondition.PROPERTY, "true"))
        ).isTrue();
        assertThat(
            GeneratedCrudLockdownCondition.isEnabled(env("prod").withProperty(GeneratedCrudLockdownCondition.PROPERTY, "false"))
        ).isFalse();
    }
}
