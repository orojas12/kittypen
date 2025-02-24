package dev.oscarrojas.kittypen.core;

import dev.oscarrojas.kittypen.core.canvas.Canvas;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Optional<Room> getRoom(String roomId) {
        Optional<RoomState> stateOpt = repository.get(roomId);

        if (stateOpt.isPresent()) {
            RoomState state = stateOpt.get();
            Canvas canvas = new Canvas(
                state.getCanvasWidth(),
                state.getCanvasHeight(),
                state.getCanvasData()
            );
            Room room = new BasicRoom(
                state.getId(),
                canvas,
                state.getClients(),
                state.getCommandStrategy()
            );
            return Optional.of(room);
        } else {
            return Optional.empty();
        }

    }

    /**
     * Gets the room this client belongs to.
     *
     * @param clientId client id
     * @return the room this client belongs to, or registers it to an available
     * room and returns it if it doesn't belong to one
     */
    public Optional<Room> getClientRoom(String clientId) {
        Optional<RoomState> stateOpt = repository.getByClientId(clientId);
        Room room = null;

        if (stateOpt.isPresent()) {
            RoomState state = stateOpt.get();
            Canvas canvas = new Canvas(
                state.getCanvasWidth(),
                state.getCanvasHeight(),
                state.getCanvasData()
            );
            room = new BasicRoom(
                state.getId(),
                canvas,
                state.getClients(),
                state.getCommandStrategy()
            );
        }

        return Optional.ofNullable(room);
    }

    private RoomState getRoomState(Room room) {
        return new RoomState(
            room.getId(),
            room.getCanvas(),
            room.getClients(),
            room.getCommandStrategy()
        );
    }

    /**
     * Finds an available room for a new client. Attempts to find the room with the
     * greatest number of clients that isn't full. If no room is available, creates a new
     * one.
     *
     * @return An available room that a new client may be added to.
     */
    public Room findAvailableRoom(String commandStrategy) {
        List<RoomState> rooms = repository.getAllByClientCountRange(0, 9);
        List<RoomState> roomTypeRooms =
            rooms.stream().filter(room -> room.getCommandStrategy().equals(commandStrategy))
                .toList();
        RoomState optimalRoom = null;
        for (RoomState room : roomTypeRooms) {
            // prioritize rooms that are closer to being full
            if (optimalRoom == null ||
                room.getClients().size() > optimalRoom.getClients().size()
            ) {
                optimalRoom = room;
            }
        }

        if (optimalRoom == null) {
            optimalRoom = new RoomState(
                UUID.randomUUID().toString(),
                new Canvas(1000, 1000),
                new HashSet<>(),
                commandStrategy
            );
        }

        Canvas canvas = new Canvas(
            optimalRoom.getCanvasWidth(),
            optimalRoom.getCanvasHeight(),
            optimalRoom.getCanvasData()
        );

        return new BasicRoom(
            optimalRoom.getId(),
            canvas,
            optimalRoom.getClients(),
            optimalRoom.getCommandStrategy()
        );
    }

    /**
     * Saves the room's current state
     *
     * @param room the room to save
     */
    public void saveRoom(Room room) {
        repository.save(getRoomState(room));
    }

}
