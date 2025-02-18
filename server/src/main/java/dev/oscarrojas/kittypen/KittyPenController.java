package dev.oscarrojas.kittypen;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.ClientDetails;
import dev.oscarrojas.kittypen.core.io.CommandRequest;
import dev.oscarrojas.kittypen.core.io.CommandRequestStrategy;
import dev.oscarrojas.kittypen.core.io.CommandRequestStrategyFactory;
import dev.oscarrojas.kittypen.core.io.CommandResponse;

import java.util.Optional;

public class KittyPenController {

    private final RoomService roomService;
    private final CommandRequestStrategyFactory strategyFactory;

    public KittyPenController(
        RoomService roomService,
        CommandRequestStrategyFactory strategyFactory
    ) {
        this.roomService = roomService;
        this.strategyFactory = strategyFactory;
    }

    public Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> request) {
        Optional<Room> roomOpt = roomService.getClientRoom(request.getClientId());

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        CommandRequestStrategy strategy = strategyFactory.getCommandStrategy(room.getStrategy());

        if (strategy == null) {
            return Optional.empty();
        }

        Optional<CommandResponse<?>> responseOpt = strategy.handleCommandRequest(request);
        return responseOpt;
    }

    public Optional<CommandResponse<?>> registerClient(
        String strategy,
        ClientDetails clientDetails
    ) {
        CommandRequestStrategy requestStrategy = strategyFactory.getCommandStrategy(strategy);

        if (requestStrategy == null) {
            return Optional.empty();
        }

        requestStrategy.registerClient(clientDetails);

    }
}
