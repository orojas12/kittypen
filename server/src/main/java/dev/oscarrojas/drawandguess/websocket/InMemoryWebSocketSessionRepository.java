package dev.oscarrojas.drawandguess.websocket;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import org.springframework.web.socket.WebSocketSession;

public class InMemoryWebSocketSessionRepository implements WebSocketSessionRepository {

    private final Map<String, WebSocketSession> sessions;

    public InMemoryWebSocketSessionRepository(Map<String, WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    @Nullable
    public WebSocketSession getById(String id) {
        return sessions.get(id);
    }

    @Override
    public Set<WebSocketSession> getAllById(Iterable<String> ids) {
        return Set.of();
    }

    @Override
    public void deleteById(String id) {}
}
