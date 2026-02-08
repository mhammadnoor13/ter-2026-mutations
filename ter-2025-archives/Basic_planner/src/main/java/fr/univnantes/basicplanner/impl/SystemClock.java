package fr.univnantes.basicplanner.impl;

import java.time.Instant;

import fr.univnantes.basicplanner.Clock;

/**
 * An implementation of {@link Clock} that relies on the system clock to get the
 * current time.
 */
public class SystemClock implements Clock {

    @Override
    public Instant getCurrentTime() {
        return Instant.now();
    }

}
