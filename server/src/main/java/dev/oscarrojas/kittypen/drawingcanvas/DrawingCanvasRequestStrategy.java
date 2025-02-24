package dev.oscarrojas.kittypen.drawingcanvas;

import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.io.Command;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandRequestStrategy;
import dev.oscarrojas.kittypen.core.io.CommandResponse;
import dev.oscarrojas.kittypen.drawingcanvas.commands.DrawCanvasFrameCommand;
import dev.oscarrojas.kittypen.drawingcanvas.commands.RemoveClientCommand;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DrawingCanvasRequestStrategy implements CommandRequestStrategy {

    final static String name = "drawing_canvas";

    private final Map<String, Command> commandMap = new HashMap<>();

    public DrawingCanvasRequestStrategy(RoomService roomService) {
        addCommand(new RemoveClientCommand(roomService));
        addCommand(new DrawCanvasFrameCommand(roomService));
    }

    protected void addCommand(Command command) {
        commandMap.put(command.getName(), command);
    }

    @Override
    public String getStrategyName() {
        return name;
    }

    @Override
    public Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> request) {
        Command command = commandMap.get(request.getCommand());

        if (command == null) {
            return Optional.empty();
        }

        if (request.getPayload() instanceof byte[]) {
            command.setBinaryRequestData((CommandRequest<byte[]>) request);
        } else {
            command.setRequestData((CommandRequest<Map<String, Object>>) request);
        }

        Optional<CommandResponse<?>> response = command.execute();
        command.clearRequestData();

        return response;
    }

}
