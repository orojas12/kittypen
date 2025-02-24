package dev.oscarrojas.kittypen.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.oscarrojas.kittypen.websocket.protocol.WebSocketCommandMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Controller
public class WebSocketServer extends AbstractWebSocketHandler {

    private final WebSocketSessionRepository sessions;
    private final WebSocketCommandMapper mapper;

    public WebSocketServer(WebSocketSessionRepository sessions) {
        this.sessions = sessions;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper = new WebSocketCommandMapper(objectMapper);
    }

    void trySendMessage(WebSocketMessage<?> message, List<WebSocketSession> recipients) {
        for (WebSocketSession session : recipients) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    removeSession(session.getId());
                    if (session.isOpen()) {
                        try {
                            session.close(CloseStatus.SERVER_ERROR);
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        }
    }

    void removeSession(String sessionId) {
        sessions.deleteById(sessionId);
    }

    @Override
    protected void handleTextMessage(
        WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    @Override
    protected void handlePongMessage(
        WebSocketSession session, PongMessage message) throws Exception {
    }

}
