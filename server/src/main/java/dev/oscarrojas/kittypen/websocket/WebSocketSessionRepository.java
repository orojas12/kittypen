package dev.oscarrojas.kittypen.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public interface WebSocketSessionRepository {

    WebSocketSession getById(String id);

    Set<WebSocketSession> getAllById(Iterable<String> ids);

    void deleteById(String id);

}
