package dev.oscarrojas.kittypen.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.oscarrojas.kittypen.KittyPenService;
import dev.oscarrojas.kittypen.core.command.CommandMessage;
import dev.oscarrojas.kittypen.core.command.CommandRequest;
import dev.oscarrojas.kittypen.core.command.CommandResponse;
import dev.oscarrojas.kittypen.websocket.protocol.CommandMessageMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
public class WebSocketService extends AbstractWebSocketHandler {

    private final WebSocketSessionRepository sessions;
    private final CommandMessageMapper mapper;
    private final KittyPenService service;

    public WebSocketService(WebSocketSessionRepository sessions, KittyPenService service) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mapper = new CommandMessageMapper(objectMapper);
        this.service = service;
        this.sessions = sessions;
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
        CommandRequest<Map<String, Object>> request =
            (CommandRequest<Map<String, Object>>) mapper.fromJson(message.getPayload());
        request.setClientId(session.getId());
        Optional<CommandResponse<?>> responseOpt = service.handleCommandRequest(request);

        if (responseOpt.isPresent()) {
            CommandResponse<?> response = responseOpt.get();
            handleCommandResponse(response);
        }
    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession session, BinaryMessage message) throws Exception {
        CommandRequest<byte[]> request =
            (CommandRequest<byte[]>) mapper.fromBytes(message.getPayload());
        request.setClientId(session.getId());
        Optional<CommandResponse<?>> responseOpt = service.handleCommandRequest(request);

        if (responseOpt.isPresent()) {
            CommandResponse<?> response = responseOpt.get();
            handleCommandResponse(response);
        }
    }

    void handleCommandResponse(CommandResponse<?> response) {
        WebSocketMessage<?> wsMessage;

        if (response.getPayload() instanceof byte[]) {
            wsMessage = new BinaryMessage(mapper.toBytes((CommandMessage<byte[]>) response));
        } else {
            try {
                wsMessage = new TextMessage(mapper.toJson(response));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        Set<WebSocketSession> clients = sessions.getAllById(response.getTargetClients());
        for (WebSocketSession client : clients) {
            try {
                client.sendMessage(wsMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void handlePongMessage(
        WebSocketSession session, PongMessage message) throws Exception {
    }

}
