package dev.oscarrojas.kittypen.drawing.commands;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.client.ClientDetails;
import dev.oscarrojas.kittypen.core.io.Command;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandResponse;

import java.util.Optional;

public class RegisterClientCommand implements Command {

    private static final String name = "register_client";
    private final RoomService roomService;
    private ClientDetails clientDetails;

    public RegisterClientCommand(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRequestData(CommandRequest<?> request) {
        this.clientDetails = (ClientDetails) request.getPayload();
    }

    @Override
    public void clearRequestData() {
        clientDetails = null;
    }

    @Override
    public Optional<CommandResponse<?>> execute() {
        Optional<Room> roomOpt = roomService.getClientRoom(clientDetails.getId());

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        room.addClient(new Client(clientDetails.getId(), clientDetails.getUsername()));
        roomService.saveRoom(room);

        return Optional.empty();
    }
}
