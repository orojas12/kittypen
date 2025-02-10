package dev.oscarrojas.kittypen.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.EventMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoomService {

    private RoomDao dao;
    private EventMapper mapper;

    public RoomService(RoomDao dao, ObjectMapper objectMapper) {
        this.dao = dao;
        this.mapper = new EventMapper(objectMapper);
    }

    public Optional<Room> getRoom(String roomId) {
        Optional<RoomState> stateOpt = dao.get(roomId);

        if (stateOpt.isPresent()) {
            RoomState state = stateOpt.get();
            Canvas canvas = new Canvas(
                state.getCanvasWidth(),
                state.getCanvasHeight(),
                state.getCanvasData()
            );
            Room room = new BasicRoom(state.getId(), canvas, state.getClients(), mapper);
            return Optional.of(room);
        } else {
            return Optional.empty();
        }

    }

    /**
     * Gets the session this websocket connection belongs to.
     *
     * @param client websocket client connection
     * @return the room this client belongs to, or registers it to an available
     * room and returns it if it doesn't belong to one
     */
    public Room getRoom(WebSocketSession client) {
        Optional<RoomState> stateOpt = dao.getByClient(client);
        Room room;

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
                mapper
            );
        } else {
            room = findAvailableRoom();
            room.addClient(client);
            dao.save(room.getState());
        }

        return room;
    }

    /**
     * Finds an available room for a new client. Attempts to find the room with the
     * greatest number of clients that isn't full. If no room is available, creates a new
     * one.
     *
     * @return An available room that a new client may be added to.
     */
    private Room findAvailableRoom() {
        List<RoomState> rooms = dao.getAllByClientCountRange(0, 9);
        RoomState optimalRoom = null;
        for (RoomState room : rooms) {
            // prioritize sessions that are closer to being full
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
                new HashSet<>()
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
            mapper
        );
    }

    /**
     * Saves the room's current state
     *
     * @param room the room to save
     */
    public void saveRoom(Room room) {
        dao.save(room.getState());
    }

}
