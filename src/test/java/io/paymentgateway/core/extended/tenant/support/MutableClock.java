package io.paymentgateway.core.extended.tenant.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * A clock tests can move forward, to exercise grace periods without sleeping.
 *
 * <p>Always holds whole microseconds: PostgreSQL stores timestamps at microsecond precision and rounds anything finer, so a
 * nanosecond-precise "now" could be stored up to half a microsecond later than the value the test compares against, making
 * exact-boundary assertions (e.g. "refused at exactly graceExpiresAt") fail about half the time.
 */
public class MutableClock extends Clock {

    private volatile Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

    public void reset() {
        now = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void advance(Duration duration) {
        now = now.plus(duration);
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return now;
    }
}
