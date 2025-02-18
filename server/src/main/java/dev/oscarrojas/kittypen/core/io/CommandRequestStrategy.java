package dev.oscarrojas.kittypen.core.io;

import dev.oscarrojas.kittypen.core.client.ClientDetails;

import java.util.Optional;

public interface CommandRequestStrategy {

    String getStrategyName();

    Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> command);

    void registerClient(ClientDetails clientDetails);
}
