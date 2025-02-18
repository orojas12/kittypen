package dev.oscarrojas.kittypen.drawing.commands;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.io.Command;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveClientCommand implements Command {

    private static final String name = "remove_client";

    private final RoomService roomService;
    private String clientId;

    public RemoveClientCommand(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRequestData(CommandRequest<?> request) {
        clientId = request.getClientId();
    }

    @Override
    public void clearRequestData() {
        clientId = null;
    }

    @Override
    public Optional<CommandResponse<?>> execute() {
        Optional<Room> roomOpt = roomService.getClientRoom(clientId);

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        room.removeClient(clientId);
        Set<String> targetClients = room.getClients().stream()
            .map(Client::getId)
            .collect(Collectors.toSet());
        roomService.saveRoom(room);

        return Optional.of(new CommandResponse<Map<String, Object>>(
            Instant.now(),
            name,
            targetClients,
            Map.of("client_id", clientId)
        ));
    }
}
