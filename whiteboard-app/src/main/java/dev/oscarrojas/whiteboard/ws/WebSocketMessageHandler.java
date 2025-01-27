package dev.oscarrojas.whiteboard.ws;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.messaging.AppMessageBroker;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
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

    private AppMessageBroker messageBroker;
    private AppSessionService sessionService;
    private AppMessageBinaryEncoder encoder;

    public WebSocketMessageHandler(
        AppMessageBroker messageBroker,
        AppSessionService sessionService,
        AppMessageBinaryEncoder encoder
    ) {
        this.messageBroker = messageBroker;
        this.sessionService = sessionService;
        this.encoder = encoder;
    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession ws, BinaryMessage message) {

        AppMessage appMessage;
        try {
            appMessage = encoder.decode(message.getPayload());
        } catch (BinaryDecodingException e) {
            tryCloseWithStatus(ws, CloseStatus.PROTOCOL_ERROR);
            return;
        }

        messageBroker.publish(appMessage.getChannel(), appMessage, ws);
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
