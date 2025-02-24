package dev.oscarrojas.kittypen.core;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {

    Optional<RoomState> get(String id);

    Optional<RoomState> getByClientId(String clientId);

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
