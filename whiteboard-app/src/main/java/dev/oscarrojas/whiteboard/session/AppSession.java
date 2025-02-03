package dev.oscarrojas.whiteboard.session;

import dev.oscarrojas.whiteboard.canvas.Canvas;
import dev.oscarrojas.whiteboard.messaging.AppEvent;
import dev.oscarrojas.whiteboard.ws.protocol.AppEventBinaryConverter;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppSession {

    private String id;
    private Canvas canvas;
    private AppEventBinaryConverter converter;
    private Map<String, WebSocketSession> connections = new HashMap<>();

    public AppSession(String id, Canvas canvas, AppEventBinaryConverter converter) {
        this.id = id;
        this.canvas = canvas;
        this.converter = converter;
    }

    public AppSession(
        String id, Canvas canvas,
        Map<String, WebSocketSession> connections, AppEventBinaryConverter converter
    ) {
        this.id = id;
        this.canvas = canvas;
        this.connections = new HashMap<>(connections);
        this.converter = converter;

    }

    public AppSession(AppSession session) {
        this.id = session.getId();
        this.canvas = new Canvas(session.getCanvas());
        this.connections = new HashMap<>(session.getConnections());
        this.converter = session.getConverter();
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

    public void broadcastEvent(AppEvent event) throws IOException {
        broadcastEvent(event, List.of());
    }

    public void broadcastEvent(AppEvent event, List<String> exclude) {
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

    public void sendEvent(String connectionId, AppEvent event) {
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

    public WebSocketSession removeConnection(WebSocketSession ws) {
        return connections.remove(ws.getId());
    }

    private void trySendEvent(WebSocketSession ws, BinaryMessage message) {
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
