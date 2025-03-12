package dev.oscarrojas.kittypen.core.io;

import java.time.Instant;
import java.util.Set;

public class CommandResponse<T> extends CommandMessage<T> {

    private Set<String> targetClients;

    public CommandResponse(
        Instant timestamp,
        String command,
        Set<String> targetClients,
        T payload
    ) {
        super(timestamp, command, payload);
        this.targetClients = targetClients;
    }

    public Set<String> getTargetClients() {
        return targetClients;
    }

    public void setTargetClients(Set<String> targetClients) {
        this.targetClients = targetClients;
    }

}
