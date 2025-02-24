package dev.oscarrojas.kittypen.websocket.protocol;

import java.time.Instant;

public class WebSocketCommandRequest<T> {
    private Instant timestamp;
    private String command;
    private T payload;

    public WebSocketCommandRequest(Instant timestamp, String command, T payload) {
        this.timestamp = timestamp;
        this.command = command;
        this.payload = payload;
    }

    public WebSocketCommandRequest() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
