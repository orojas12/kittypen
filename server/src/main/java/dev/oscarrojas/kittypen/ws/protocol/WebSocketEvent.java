package dev.oscarrojas.kittypen.ws.protocol;

import java.time.Instant;

public class WebSocketEvent<T> {
    private Instant timestamp;
    private String name;
    private T payload;

    public WebSocketEvent(Instant timestamp, String name, T payload) {
        this.timestamp = timestamp;
        this.name = name;
        this.payload = payload;
    }

    public WebSocketEvent() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
