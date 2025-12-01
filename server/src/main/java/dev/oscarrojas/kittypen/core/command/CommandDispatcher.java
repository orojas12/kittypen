package dev.oscarrojas.kittypen.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandDispatcher {

    private Map<String, Command> commandRegistry = new HashMap<>();

    public void addCommand(Command command) {
        commandRegistry.put(command.getName(), command);
    }

    public Optional<CommandResponse<?>> dispatch(CommandRequest<?> request) {
        /**
         * if request is binary
         *      get binary command
         *      execute command with request data
         * else
         *      get object command
         *      execute command with request data
         */
    }

}
