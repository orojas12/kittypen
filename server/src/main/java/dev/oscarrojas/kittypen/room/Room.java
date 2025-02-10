package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.Event;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Set;

public interface Room {

    String getId();

    Canvas getCanvas();

    Set<WebSocketSession> getClients();

    void addClient(WebSocketSession ws);

    boolean hasClient(String clientId);

    RoomState getState();

    void handleClientEvent(Event<?> event, WebSocketSession client);

    void broadcastEvent(Event<?> event, List<String> exclude);

    void sendEvent(String clientId, Event<?> event);

    WebSocketSession removeClient(WebSocketSession ws);

}
