package dev.oscarrojas.kittypen.core;

import dev.oscarrojas.kittypen.core.client.Client;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, RoomState> rooms = new ConcurrentHashMap<>();

    @Override
    public Optional<RoomState> get(String id) {
        RoomState state = rooms.get(id);
        return Optional.ofNullable(state);
    }

    @Override
    public Optional<RoomState> getByClientId(String clientId) {
        RoomState state = null;
        for (RoomState roomState : rooms.values()) {
            for (Client client : roomState.getClients()) {
                if (client.getId().equals(clientId)) {
                    state = roomState;
                    break;
                }
            }
        }
        return Optional.ofNullable(state);
    }

    @Override
    public List<RoomState> getAllByClientCountRange(int min, int max) {
        return rooms.values().stream().filter(roomState -> {
            int clientCount = roomState.getClients().size();
            return clientCount >= min && clientCount <= max;
        }).toList();
    }

    @Override
    public void save(RoomState state) {
        rooms.put(state.getId(), state);
    }

    @Override
    public void delete(String roomId) {
        RoomState state = rooms.remove(roomId);
    }

}
