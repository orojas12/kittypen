package dev.oscarrojas.whiteboard.ws;

import dev.oscarrojas.whiteboard.messaging.AppEvent;
import dev.oscarrojas.whiteboard.messaging.AppEventEmitter;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import dev.oscarrojas.whiteboard.ws.protocol.AppEventBinaryConverter;
import dev.oscarrojas.whiteboard.ws.protocol.BinaryDecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Optional;

@Component
public class WebSocketMessageHandler extends BinaryWebSocketHandler {

    private AppEventEmitter eventEmitter;
    private AppSessionService sessionService;
    private AppEventBinaryConverter converter;

    public WebSocketMessageHandler(
        AppEventEmitter eventEmitter,
        AppSessionService sessionService,
        AppEventBinaryConverter converter
    ) {
        this.eventEmitter = eventEmitter;
        this.sessionService = sessionService;
        this.converter = converter;
    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession ws, BinaryMessage message) {

        AppEvent event;
        try {
            event = converter.fromBytes(message.getPayload());
        } catch (BinaryDecodingException e) {
            tryCloseWithStatus(ws, CloseStatus.PROTOCOL_ERROR);
            return;
        }

        eventEmitter.emit(event.getName(), event, ws);
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
