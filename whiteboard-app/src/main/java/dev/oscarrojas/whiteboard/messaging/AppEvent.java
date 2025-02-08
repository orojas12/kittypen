package dev.oscarrojas.whiteboard.messaging;

import java.time.Instant;

public interface AppEvent<T> {

    Instant getTimestamp();

    String getName();

    T getPayload();

}
