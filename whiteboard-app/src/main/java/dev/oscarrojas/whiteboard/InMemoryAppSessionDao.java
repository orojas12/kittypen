package dev.oscarrojas.whiteboard;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAppSessionDao implements AppSessionDao {

    private final Map<String, AppSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<AppSession> get(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public Optional<AppSession> getByConnectionId(String connectionId) {
        AppSession session = null;

        for (AppSession sessionEntry : sessions.values()) {
            if (sessionEntry.hasConnection(connectionId)) {
                session = sessionEntry;
                break;
            }
        }

        return Optional.ofNullable(session);
    }

    @Override
    public List<AppSession> getAllByConnectionCountRange(int min, int max) {
        return sessions.values().stream().filter((session) -> {
            int connections = session.getConnectionCount();
            return connections >= min && connections <= max;
        }).toList();
    }

    @Override
    public void save(AppSession session) {
        sessions.put(session.getId(), new AppSession(session));
    }
}
