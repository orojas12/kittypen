package dev.oscarrojas.kittypen.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.oscarrojas.kittypen.event.RoomEvent;
import dev.oscarrojas.kittypen.event.RoomEventStrategy;
import dev.oscarrojas.kittypen.ws.protocol.WebSocketEvent;
import dev.oscarrojas.kittypen.ws.protocol.WebSocketEventMapper;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebSocketRoomEventStrategy implements RoomEventStrategy {

    private final WebSocketSessionRepository sessions;
    private final WebSocketEventMapper mapper;

    public WebSocketRoomEventStrategy(
        WebSocketSessionRepository sessions,
        WebSocketEventMapper mapper
    ) {
        this.sessions = sessions;
        this.mapper = mapper;
    }

    @Override
    public void handleRoomEvent(RoomEvent<?> event) {
        Set<WebSocketSession> recipients = sessions.getAllById(event.getRecipients());
        for (WebSocketSession ws : recipients) {

        }
    }

    public void broadcastEvent(WebSocketEvent<?> webSocketEvent, List<String> exclude) {
        if (webSocketEvent.getPayload() instanceof byte[]) {
            WebSocketEvent<byte[]> binaryWebSocketEvent = (WebSocketEvent<byte[]>) webSocketEvent;
            BinaryMessage message = new BinaryMessage(mapper.toBytes(binaryWebSocketEvent));
            tryBroadcastMessage(message, exclude);
        } else {
            WebSocketEvent<Map<String, Object>> jsonWebSocketEvent = (WebSocketEvent<Map<String, Object>>) webSocketEvent;
            try {
                TextMessage message = new TextMessage(mapper.toJson(jsonWebSocketEvent));
                tryBroadcastMessage(message, exclude);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendEvent(String clientId, WebSocketEvent<?> webSocketEvent) {
        for (WebSocketSession ws : sessions) {
            if (ws.getId().equals(clientId)) {
                if (webSocketEvent.getPayload() instanceof byte[]) {
                    WebSocketEvent<byte[]> binaryWebSocketEvent = (WebSocketEvent<byte[]>) webSocketEvent;
                    BinaryMessage message = new BinaryMessage(mapper.toBytes(binaryWebSocketEvent));
                    trySendMessage(ws, message);
                } else {
                    WebSocketEvent<Map<String, Object>> jsonWebSocketEvent = (WebSocketEvent<Map<String, Object>>) webSocketEvent;
                    try {
                        TextMessage message = new TextMessage(mapper.toJson(jsonWebSocketEvent));
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
    public void removeClient(String clientId) {
        sessions.remove(clientId);
    }

    private void tryBroadcastMessage(WebSocketMessage<?> message, List<String> exclude) {
        for (WebSocketSession client : sessions) {
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
