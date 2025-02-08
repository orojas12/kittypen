package dev.oscarrojas.whiteboard.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.whiteboard.messaging.AppEventEmitter;
import dev.oscarrojas.whiteboard.messaging.BinaryAppEvent;
import dev.oscarrojas.whiteboard.messaging.JsonAppEvent;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import dev.oscarrojas.whiteboard.ws.protocol.AppEventBinaryConverter;
import dev.oscarrojas.whiteboard.ws.protocol.BinaryDecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Optional;

@Component
public class WebSocketMessageHandler extends BinaryWebSocketHandler {

    private AppEventEmitter eventEmitter;
    private AppSessionService sessionService;
    private AppEventBinaryConverter converter;
    private ObjectMapper jsonMapper;

    public WebSocketMessageHandler(
        AppEventEmitter eventEmitter,
        AppSessionService sessionService,
        AppEventBinaryConverter converter,
        ObjectMapper jsonMapper
    ) {
        this.eventEmitter = eventEmitter;
        this.sessionService = sessionService;
        this.converter = converter;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession ws, BinaryMessage message) {

        BinaryAppEvent event;
        try {
            event = converter.fromBytes(message.getPayload());
        } catch (BinaryDecodingException e) {
            tryCloseWithStatus(ws, CloseStatus.PROTOCOL_ERROR);
            return;
        }

        eventEmitter.emit(event.getName(), event, ws);
    }

    @Override
    protected void handleTextMessage(WebSocketSession ws, TextMessage message) {
        try {
            JsonAppEvent event = jsonMapper.readValue(
                message.getPayload(),
                JsonAppEvent.class
            );
            eventEmitter.emit(event.getName(), event, ws);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession ws) {
        // register new connection to an available session
        sessionService.getSession(ws);
    }

    private void tryCloseWithStatus(WebSocketSession ws, CloseStatus status) {
        try {
            ws.close(status);
        } catch (IOException e) {
            // ignore
        } finally {
            // clean up dead connection
            Optional<AppSession> session = sessionService.getSession(ws.getId());
            session.ifPresent(appSession -> appSession.removeConnection(ws));
        }
    }
}
