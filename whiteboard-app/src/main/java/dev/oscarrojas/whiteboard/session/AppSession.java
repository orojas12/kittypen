package dev.oscarrojas.whiteboard.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.whiteboard.canvas.Canvas;
import dev.oscarrojas.whiteboard.messaging.BinaryAppEvent;
import dev.oscarrojas.whiteboard.messaging.JsonAppEvent;
import dev.oscarrojas.whiteboard.ws.protocol.AppEventBinaryConverter;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppSession {

    private final Canvas canvas;
    private final AppEventBinaryConverter converter;
    private final ObjectMapper mapper;
    private String id;
    private Map<String, WebSocketSession> connections = new HashMap<>();

    public AppSession(
        String id, Canvas canvas, AppEventBinaryConverter converter,
        ObjectMapper mapper
    ) {
        this.id = id;
        this.canvas = canvas;
        this.converter = converter;
        this.mapper = mapper;
    }

    public AppSession(
        String id, Canvas canvas,
        Map<String, WebSocketSession> connections, AppEventBinaryConverter converter,
        ObjectMapper mapper
    ) {
        this.id = id;
        this.canvas = canvas;
        this.connections = new HashMap<>(connections);
        this.converter = converter;
        this.mapper = mapper;
    }

    public AppSession(AppSession session) {
        this.id = session.getId();
        this.canvas = new Canvas(session.getCanvas());
        this.connections = new HashMap<>(session.getConnections());
        this.converter = session.getConverter();
        this.mapper = session.getMapper();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public AppEventBinaryConverter getConverter() {
        return converter;
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    public Map<String, WebSocketSession> getConnections() {
        return connections;
    }

    public void addConnection(WebSocketSession conn) {
        connections.put(conn.getId(), conn);
    }

    public boolean hasConnection(String connectionId) {
        return connections.containsKey(connectionId);
    }

    public int getConnectionCount() {
        return connections.size();
    }

    public AppSessionDetails getDetails() {
        List<String> users = connections.values().stream()
            .map(ws -> ((String) ws.getAttributes().get("username"))).toList();
        return new AppSessionDetails(id, users);
    }

    public void broadcastEvent(BinaryAppEvent event) throws IOException {
        broadcastEvent(event, List.of());
    }

    public void broadcastEvent(BinaryAppEvent event, List<String> exclude) {
        BinaryMessage binaryMessage = new BinaryMessage(converter.toBytes(event));

        for (WebSocketSession connection : connections.values()) {
            if (exclude.contains(connection.getId())) {
                continue;
            }

            if (connection.isOpen()) {
                trySendEvent(connection, binaryMessage);
            }
        }
    }

    public void sendEvent(String connectionId, BinaryAppEvent event) {
        WebSocketSession connection = connections.get(connectionId);

        if (connection == null) {
            return;
        }

        BinaryMessage binaryMessage = new BinaryMessage(converter.toBytes(event));

        if (connection.isOpen()) {
            trySendEvent(connection, binaryMessage);
        } else {
            WebSocketSession ws = removeConnection(connection);
        }
    }

    public void sendEvent(String connectionId, JsonAppEvent event) {
        WebSocketSession connection = connections.get(connectionId);

        if (connection == null) {
            return;
        }

        TextMessage message;

        try {
            message = new TextMessage(mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }

        if (connection.isOpen()) {
            trySendEvent(connection, message);
        } else {
            WebSocketSession ws = removeConnection(connection);
        }
    }

    public WebSocketSession removeConnection(WebSocketSession ws) {
        return connections.remove(ws.getId());
    }

    private void trySendEvent(WebSocketSession ws, WebSocketMessage<?> message) {
        try {
            ws.sendMessage(message);
        } catch (IOException e) {
            WebSocketSession removed = removeConnection(ws);
            if (removed.isOpen()) {
                try {
                    removed.close(CloseStatus.SERVER_ERROR);
                } catch (Throwable t) {
                    // ignore
                }
            }
        }

    }
}
