package dev.oscarrojas.kittypen.drawingcanvas.commands;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.client.ClientRegistration;
import dev.oscarrojas.kittypen.core.io.Command;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandResponse;

import java.util.Optional;

public class RegisterClientCommand implements Command {

    private static final String name = "register_client";
    private final RoomService roomService;
    private ClientRegistration clientRegistration;

    public RegisterClientCommand(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRequestData(CommandRequest<?> request) {
        this.clientRegistration = (ClientRegistration) request.getPayload();
    }

    @Override
    public void clearRequestData() {
        clientRegistration = null;
    }

    @Override
    public Optional<CommandResponse<?>> execute() {
        Optional<Room> roomOpt = roomService.getClientRoom(clientRegistration.getId());

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        room.addClient(new Client(clientRegistration.getId(), clientRegistration.getUsername()));
        roomService.saveRoom(room);

        return Optional.empty();
    }
}
