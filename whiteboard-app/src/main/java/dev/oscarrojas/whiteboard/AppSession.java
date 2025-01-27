package dev.oscarrojas.whiteboard;

import dev.oscarrojas.whiteboard.canvas.Canvas;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
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
    private AppMessageBinaryEncoder encoder;
    private Map<String, WebSocketSession> connections = new HashMap<>();

    public AppSession(String id, Canvas canvas, AppMessageBinaryEncoder encoder) {
        this.id = id;
        this.canvas = canvas;
        this.encoder = encoder;
    }

    public AppSession(
        String id, Canvas canvas,
        Map<String, WebSocketSession> connections, AppMessageBinaryEncoder encoder
    ) {
        this.id = id;
        this.canvas = canvas;
        this.connections = new HashMap<>(connections);
        this.encoder = encoder;

    }

    public AppSession(AppSession session) {
        this.id = session.getId();
        this.canvas = new Canvas(session.getCanvas());
        this.connections = new HashMap<>(session.getConnections());
        this.encoder = session.getEncoder();
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

    public AppMessageBinaryEncoder getEncoder() {
        return encoder;
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

    public void broadcastMessage(AppMessage message) throws IOException {
        broadcastMessage(message, List.of());
    }

    public void broadcastMessage(AppMessage message, List<String> exclude) {
        BinaryMessage binaryMessage = new BinaryMessage(encoder.encode(message));

        for (WebSocketSession connection : connections.values()) {
            if (exclude.contains(connection.getId())) {
                continue;
            }

            trySendMessage(connection, binaryMessage);
        }
    }

    public void sendMessage(String connectionId, AppMessage message) {
        WebSocketSession connection = connections.get(connectionId);

        if (connection == null) {
            return;
        }

        BinaryMessage binaryMessage = new BinaryMessage(encoder.encode(message));

        trySendMessage(connection, binaryMessage);
    }

    public WebSocketSession removeConnection(WebSocketSession ws) {
        return connections.remove(ws.getId());
    }

    private void trySendMessage(WebSocketSession ws, BinaryMessage message) {
        try {
            ws.sendMessage(message);
        } catch (IOException e) {
            WebSocketSession removed = connections.remove(ws.getId());
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
