package dev.oscarrojas.kittypen.core.command;

import org.springframework.lang.Nullable;

import java.time.Instant;

public class CommandMessage<T> {

    protected Instant timestamp;
    protected String command;
    protected T payload;

    public CommandMessage() {
    }

    public CommandMessage(
        Instant timestamp,
        String command,
        T payload
    ) {
        this.timestamp = timestamp;
        this.command = command;
        this.payload = payload;
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

    public void setPayload(@Nullable T payload) {
        this.payload = payload;
    }
}
