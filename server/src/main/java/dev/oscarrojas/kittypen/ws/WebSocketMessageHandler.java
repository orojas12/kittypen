package dev.oscarrojas.kittypen.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.oscarrojas.kittypen.room.RoomService;
import dev.oscarrojas.kittypen.ws.protocol.WebSocketEvent;
import dev.oscarrojas.kittypen.ws.protocol.WebSocketEventMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.Map;

@Component
public class WebSocketMessageHandler extends BinaryWebSocketHandler {

    private final RoomService roomService;
    private final WebSocketEventMapper mapper;

    public WebSocketMessageHandler(
        RoomService roomService,
        WebSocketEventMapper mapper
    ) {
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession ws, BinaryMessage message) {
        WebSocketEvent<byte[]> webSocketEvent = mapper.fromBytes(message.getPayload());
        roomService.handleClientEvent(webSocketEvent, ws);
    }

    @Override
    protected void handleTextMessage(WebSocketSession ws, TextMessage message) {
        WebSocketEvent<Map<String, Object>> webSocketEvent;

        try {
            webSocketEvent = mapper.fromJson(message.getPayload());
        } catch (JsonProcessingException e) {
            // TODO: log critical error
            System.out.println(e.getMessage());
            return;
        }

        roomService.handleClientEvent(webSocketEvent, ws);
    }

}
