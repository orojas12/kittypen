package dev.oscarrojas.kittypen.messaging;

import java.time.Instant;

public interface AppEvent<T> {

    Instant getTimestamp();

    String getName();

    T getPayload();

}
