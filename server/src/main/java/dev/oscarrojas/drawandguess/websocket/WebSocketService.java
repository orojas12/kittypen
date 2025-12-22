package dev.oscarrojas.drawandguess.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.DrawAndGuessController;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessage;
import dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessageSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Controller
public class WebSocketService extends AbstractWebSocketHandler {

    private DrawAndGuessController controller;
    private ProtocolMessageSerializer serializer;

    public WebSocketService(DrawAndGuessController controller, ObjectMapper objectMapper) {
        this.controller = controller;
        this.serializer = new ProtocolMessageSerializer(objectMapper);
    }


    @Override
    protected void handleTextMessage(
        WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession session, BinaryMessage message) throws Exception {
        ProtocolMessage<?> protocolMessage = serializer.deserialize(message.getPayload().array());
        InboundMessage<?> inboundMessage = new InboundMessage<>(protocolMessage.action(),
                protocolMessage.timestamp(), session.getId(), protocolMessage.payload());
        controller.handleInboundMessage(inboundMessage);
    }


    @Override
    protected void handlePongMessage(
        WebSocketSession session, PongMessage message) throws Exception {
    }

}
