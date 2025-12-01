package dev.oscarrojas.kittypen;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.ClientRegistrar;
import dev.oscarrojas.kittypen.core.client.ClientRegistrarFactory;
import dev.oscarrojas.kittypen.core.client.ClientRegistration;
import dev.oscarrojas.kittypen.core.command.CommandRequest;
import dev.oscarrojas.kittypen.core.command.CommandRequestStrategy;
import dev.oscarrojas.kittypen.core.command.CommandRequestStrategyFactory;
import dev.oscarrojas.kittypen.core.command.CommandResponse;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class KittyPenService {

    private final RoomService roomService;
    private final CommandRequestStrategyFactory strategyFactory;
    private final ClientRegistrarFactory registrarFactory;

    public KittyPenService(
        RoomService roomService,
        CommandRequestStrategyFactory strategyFactory,
        ClientRegistrarFactory registrarFactory
    ) {
        this.roomService = roomService;
        this.strategyFactory = strategyFactory;
        this.registrarFactory = registrarFactory;
    }

    /**
     * Main entrypoint for all client commands. Routes commands to the command strategy
     * used by the room the client belongs to.
     *
     * @param request command request
     * @return optional command response returned by the command strategy
     */
    public Optional<CommandResponse<?>> handleCommandRequest(CommandRequest<?> request) {
        Optional<Room> roomOpt = roomService.getClientRoom(request.getClientId());

        if (roomOpt.isEmpty()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        CommandRequestStrategy strategy = strategyFactory.getCommandStrategy(
            room.getCommandStrategy());

        if (strategy == null) {
            return Optional.empty();
        }

        return strategy.handleCommandRequest(request);
    }

    /**
     * Registers a new client with the application.
     *
     * @param registration registration details such as the name of the client registrar to use.
     */
    public void registerClient(ClientRegistration registration) {
        String registrarName = registration.getRegistrar();
        ClientRegistrar registrar = registrarFactory.getRegistrar(registrarName);

        if (registrar == null) {
            throw new ClientRegistrarConfigurationException(registrarName);
        }

        registrar.registerClient(registration);
    }

    static protected class ClientRegistrarConfigurationException extends RuntimeException {
        ClientRegistrarConfigurationException(String registrarName) {
            super("No registrar configured with name '" + registrarName + "'");
        }
    }
}
