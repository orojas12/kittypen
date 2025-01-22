package dev.oscarrojas.whiteboard;

import dev.oscarrojas.whiteboard.exception.NotFoundException;
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
        AppSession session = sessions.get(id);
        return session == null ? Optional.empty() : Optional.of(new AppSession(session));
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

        return session == null ? Optional.empty() : Optional.of(new AppSession(session));
    }

    @Override
    public List<AppSession> getAllByConnectionCountRange(int min, int max) {
        return sessions.values().stream().filter((session) -> {
            int connections = session.getConnectionCount();
            return connections >= min && connections <= max;
        }).map(AppSession::new).toList();
    }

    @Override
    public void save(AppSession session) {
        sessions.put(session.getId(), new AppSession(session));
    }

    @Override
    public void delete(String sessionId) throws NotFoundException {
        AppSession session = sessions.remove(sessionId);
        if (session == null) {
            throw new NotFoundException(String.format(
                "App session '%s' does not exist",
                sessionId
            ));
        }
    }

    public void deleteAll() {
        sessions.clear();
    }
}
