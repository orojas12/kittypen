package dev.oscarrojas.kittypen.room;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;

public interface RoomDao {

    Optional<RoomState> get(String id);

    Optional<RoomState> getByClient(WebSocketSession client);

    /**
     * Finds all rooms with a number of clients within a specified range (inclusive).
     *
     * @param min minimum number of clients
     * @param max maximum number of clients
     */
    List<RoomState> getAllByClientCountRange(int min, int max);

    void save(RoomState state);

    void delete(String roomId);

}
