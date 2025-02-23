package dev.oscarrojas.kittypen.core.io;

import java.util.Optional;

public interface CommandRequestStrategy {

    String getStrategyName();

    Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> command);

}
