package fr.univnantes.basicplanner;

import java.time.Instant;

/**
 * A service to obtain the current time.
 */
public interface Clock {

    /**
     * Retrieve the current time.
     * @return The current time.
     */
    Instant getCurrentTime();
}
