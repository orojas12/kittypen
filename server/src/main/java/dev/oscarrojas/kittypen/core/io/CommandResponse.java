package dev.oscarrojas.kittypen.core.io;

import java.time.Instant;
import java.util.Set;

public class CommandResponse<T> {

    private Instant timestamp;
    private String command;
    private Set<String> targetClients;
    private T payload;

    public CommandResponse(
        Instant timestamp,
        String command,
        Set<String> targetClients,
        T payload
    ) {
        this.timestamp = timestamp;
        this.command = command;
        this.targetClients = targetClients;
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

    public Set<String> getTargetClients() {
        return targetClients;
    }

    public void setTargetClients(Set<String> targetClients) {
        this.targetClients = targetClients;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
