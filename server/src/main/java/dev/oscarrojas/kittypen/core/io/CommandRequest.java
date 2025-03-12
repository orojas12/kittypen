package dev.oscarrojas.kittypen.core.io;

import java.time.Instant;

public class CommandRequest<T> extends CommandMessage<T> {

    private String clientId;

    public CommandRequest() {
    }

    public CommandRequest(
        Instant timestamp,
        String clientId,
        String command,
        T payload
    ) {
        super(timestamp, command, payload);
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
