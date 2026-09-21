package io.paymentgateway.core.extended.tenant.service.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

class TenantStatusMachineTest {

    /** The specification of the state machine, written independently of the implementation's table. */
    private static final Map<TenantStatus, Set<TenantStatus>> EXPECTED = Map.of(
        TenantStatus.PENDING_REVIEW,
        Set.of(TenantStatus.ACTIVE, TenantStatus.SUSPENDED, TenantStatus.REJECTED),
        TenantStatus.ACTIVE,
        Set.of(TenantStatus.SUSPENDED, TenantStatus.CLOSED),
        TenantStatus.SUSPENDED,
        Set.of(TenantStatus.ACTIVE, TenantStatus.CLOSED),
        TenantStatus.REJECTED,
        Set.of(),
        TenantStatus.CLOSED,
        Set.of()
    );

    static Stream<Arguments> everyStatusPair() {
        return EnumSet.allOf(TenantStatus.class)
            .stream()
            .flatMap(from ->
                EnumSet.allOf(TenantStatus.class)
                    .stream()
                    .map(to -> Arguments.of(from, to, EXPECTED.get(from).contains(to)))
            );
    }

    @ParameterizedTest(name = "{0} -> {1} allowed={2}")
    @MethodSource("everyStatusPair")
    void everyPairMatchesTheSpecifiedMachine(TenantStatus from, TenantStatus to, boolean allowed) {
        assertThat(TenantStatusMachine.canTransition(from, to)).isEqualTo(allowed);
        if (allowed) {
            assertThatCode(() -> TenantStatusMachine.requireTransition(from, to)).doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> TenantStatusMachine.requireTransition(from, to)).isInstanceOfSatisfying(
                TenantOperationException.class,
                e -> {
                    assertThat(e.getCode()).isEqualTo("INVALID_TENANT_TRANSITION");
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                }
            );
        }
    }

    @Test
    void coversEveryStatusSoNewEnumValuesForceThisTestToBeUpdated() {
        assertThat(EXPECTED.keySet()).containsExactlyInAnyOrder(TenantStatus.values());
    }

    @Test
    void rejectedAndClosedAreTerminal() {
        assertThat(TenantStatusMachine.allowedFrom(TenantStatus.REJECTED)).isEmpty();
        assertThat(TenantStatusMachine.allowedFrom(TenantStatus.CLOSED)).isEmpty();
    }

    @Test
    void aTenantCannotBeActivatedFromAnyTerminalState() {
        assertThat(TenantStatusMachine.canTransition(TenantStatus.CLOSED, TenantStatus.ACTIVE)).isFalse();
        assertThat(TenantStatusMachine.canTransition(TenantStatus.REJECTED, TenantStatus.ACTIVE)).isFalse();
    }
}
