package dev.oscarrojas.kittypen.core.io;

import org.springframework.lang.Nullable;

import java.time.Instant;

public class CommandRequest<T> {

    private Instant timestamp;
    private String clientId;
    private String command;
    private T payload;

    public CommandRequest(
        Instant timestamp,
        String clientId, String command, @Nullable T payload
    ) {
        this.timestamp = timestamp;
        this.clientId = clientId;
        this.command = command;
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Nullable
    public T getPayload() {
        return payload;
    }

    public void setPayload(@Nullable T payload) {
        this.payload = payload;
    }
}
