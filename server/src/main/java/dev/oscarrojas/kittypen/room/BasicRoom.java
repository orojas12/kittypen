package dev.oscarrojas.kittypen.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.Event;
import dev.oscarrojas.kittypen.ws.protocol.EventMapper;
import jakarta.annotation.Nullable;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasicRoom implements Room {

    private final String id;
    private final Canvas canvas;
    private final Set<WebSocketSession> clients;
    private final EventStrategy strategy;
    private final EventMapper mapper;

    public BasicRoom(
        String id, Canvas canvas, Set<WebSocketSession> clients,
        EventStrategy strategy, EventMapper mapper
    ) {
        this.id = id;
        this.canvas = canvas;
        this.clients = clients;
        this.strategy = strategy;
        this.mapper = mapper;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public Set<WebSocketSession> getClients() {
        return clients;
    }

    @Override
    public void addClient(WebSocketSession ws) {
        clients.add(ws);
    }

    @Override
    public boolean hasClient(String clientId) {
        return clients.stream().anyMatch(client -> client.getId().equals(clientId));
    }

    @Override
    public void handleClientEvent(Event<?> event, WebSocketSession client) {
        strategy.handleEvent(event, client, this);
    }

    @Override
    public RoomState getState() {
        return new RoomState(id, canvas, clients);
    }

    public void broadcastEvent(Event<?> event) {
        broadcastEvent(event, List.of());
    }

    @Override
    public void broadcastEvent(Event<?> event, List<String> exclude) {
        if (event.getPayload() instanceof byte[]) {
            Event<byte[]> binaryEvent = (Event<byte[]>) event;
            BinaryMessage message = new BinaryMessage(mapper.toBytes(binaryEvent));
            tryBroadcastMessage(message, exclude);
        } else {
            Event<Map<String, Object>> jsonEvent = (Event<Map<String, Object>>) event;
            try {
                TextMessage message = new TextMessage(mapper.toJson(jsonEvent));
                tryBroadcastMessage(message, exclude);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendEvent(String clientId, Event<?> event) {
        for (WebSocketSession ws : clients) {
            if (ws.getId().equals(clientId)) {
                if (event.getPayload() instanceof byte[]) {
                    Event<byte[]> binaryEvent = (Event<byte[]>) event;
                    BinaryMessage message = new BinaryMessage(mapper.toBytes(binaryEvent));
                    trySendMessage(ws, message);
                } else {
                    Event<Map<String, Object>> jsonEvent = (Event<Map<String, Object>>) event;
                    try {
                        TextMessage message = new TextMessage(mapper.toJson(jsonEvent));
                        trySendMessage(ws, message);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            break;
        }
    }

    @Override
    @Nullable
    public WebSocketSession removeClient(WebSocketSession client) {
        WebSocketSession ws = null;
        for (WebSocketSession session : clients) {
            if (session.getId().equals(client.getId())) {
                ws = session;
            }
        }
        clients.remove(client);
        return ws;
    }

    private void tryBroadcastMessage(WebSocketMessage<?> message, List<String> exclude) {
        for (WebSocketSession client : clients) {
            if (exclude.contains(client.getId())) {
                continue;
            }

            if (client.isOpen()) {
                trySendMessage(client, message);
            }
        }
    }

    private void trySendMessage(WebSocketSession ws, WebSocketMessage<?> message) {
        try {
            ws.sendMessage(message);
        } catch (IOException e) {
            WebSocketSession removed = removeClient(ws);
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
