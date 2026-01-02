package dev.oscarrojas.drawandguess.websocket;

import java.util.Set;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionRepository {

    WebSocketSession getById(String id);

    Set<WebSocketSession> getAllById(Iterable<String> ids);

    void deleteById(String id);
}
