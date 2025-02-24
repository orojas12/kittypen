package dev.oscarrojas.kittypen.core.io;

import java.util.Map;
import java.util.Optional;

public interface Command {

    String getName();

    void setRequestData(CommandRequest<Map<String, Object>> request);

    void setBinaryRequestData(CommandRequest<byte[]> request);

    void clearRequestData();

    Optional<CommandResponse<?>> execute();

}
