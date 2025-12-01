package dev.oscarrojas.kittypen.core.command;

import java.util.Optional;

public interface CommandRequestStrategy {

    String getStrategyName();

    Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> command);

}
