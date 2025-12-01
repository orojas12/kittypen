package dev.oscarrojas.kittypen.core.command;

import java.util.Map;
import java.util.Optional;

public interface ObjectCommand extends Command {

    Optional<CommandResponse<?>> execute(CommandRequest<Map<String, String>> request);

}
