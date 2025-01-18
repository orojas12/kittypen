package dev.oscarrojas.whiteboard.ws;

import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.messaging.AppMessageBroker;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

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
        WebSocketSession ws, BinaryMessage message) throws Exception {
        AppMessage appMessage = encoder.decode(message.getPayload());

        messageBroker.publish(appMessage.getChannel(), appMessage, ws);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession ws) {
        // register new connection to an available session
        sessionService.getSession(ws);
    }
}
