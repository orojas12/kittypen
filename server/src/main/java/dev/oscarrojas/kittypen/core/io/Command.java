package dev.oscarrojas.kittypen.core.io;

import java.util.Optional;

public interface Command {

    String getName();

    void setRequestData(CommandRequest<?> request);

    void clearRequestData();

    Optional<CommandResponse<?>> execute();

}
