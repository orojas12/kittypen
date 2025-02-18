package dev.oscarrojas.kittypen.drawing.commands;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.canvas.CanvasFrame;
import dev.oscarrojas.kittypen.core.canvas.CanvasFrameBinaryConverter;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.io.Command;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandResponse;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DrawCanvasFrameCommand implements Command {

    private static final String name = "draw_canvas_frame";
    private final CanvasFrameBinaryConverter frameConverter = new CanvasFrameBinaryConverter();
    private final RoomService roomService;
    private String clientId;
    private CanvasFrame frame;

    public DrawCanvasFrameCommand(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRequestData(CommandRequest<?> request) {
        clientId = request.getClientId();
        frame = frameConverter.fromBytes((byte[]) request.getPayload());
    }

    @Override
    public void clearRequestData() {
        clientId = null;
        frame = null;
    }

    @Override
    public Optional<CommandResponse<?>> execute() {
        Optional<Room> roomOpt = roomService.getClientRoom(clientId);

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();

        Canvas canvas = room.getCanvas();
        canvas.putData(frame);

        roomService.saveRoom(room);

        Set<String> targetClients = room.getClients().stream()
            .map(Client::getId)
            .filter(id -> !id.equals(clientId))
            .collect(Collectors.toSet());

        CommandResponse<byte[]> response = new CommandResponse<>(
            Instant.now(),
            name,
            targetClients,
            frameConverter.toBytes(frame)
        );

        return Optional.of(response);
    }
}
