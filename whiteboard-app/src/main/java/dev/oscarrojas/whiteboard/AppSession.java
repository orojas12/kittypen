package dev.oscarrojas.whiteboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import dev.oscarrojas.whiteboard.canvas.Canvas;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessage;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;

public class AppSession {

  private String id;
  private Canvas canvas;
  private AppMessageBinaryEncoder encoder;
  private Map<String, WebSocketSession> connections = new HashMap<>();

  public AppSession() {
  }

  public AppSession(String id, Canvas canvas, AppMessageBinaryEncoder encoder) {
    this.id = id;
    this.canvas = canvas;
    this.encoder = encoder;
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

  public void setCanvas(Canvas canvas) {
    this.canvas = canvas;
  }

  public void addConnection(WebSocketSession conn) {
    connections.put(conn.getId(), conn);
  }

  public boolean hasConnection(String connectionId) {
    return connections.containsKey(connectionId);
  }

  public void broadcastMessage(AppMessage message) throws IOException {
    broadcastMessage(message, List.of());
  }

  public void broadcastMessage(AppMessage message, List<String> exclude) throws IOException {
    BinaryMessage binaryMessage = new BinaryMessage(encoder.encode(message));

    for (WebSocketSession connection : connections.values()) {
      if (exclude.contains(connection.getId())) {
        continue;
      }

      connection.sendMessage(binaryMessage);
    }
  }

  public void sendMessage(String connectionId, AppMessage message) throws IOException {
    WebSocketSession connection = connections.get(connectionId);

    if (connection == null) {
      return;
    }

    BinaryMessage binaryMessage = new BinaryMessage(encoder.encode(message));
    connection.sendMessage(binaryMessage);
  }
}
