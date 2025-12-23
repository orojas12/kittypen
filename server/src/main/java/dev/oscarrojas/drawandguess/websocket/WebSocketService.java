package dev.oscarrojas.drawandguess.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.MessageDispatcher;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessage;
import dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessageSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Controller
public class WebSocketService extends AbstractWebSocketHandler {

    private final MessageDispatcher dispatcher;
    private final ProtocolMessageSerializer serializer;

    public WebSocketService(ObjectMapper objectMapper) {
        this.dispatcher = new MessageDispatcher();
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
        dispatcher.handleInboundMessage(inboundMessage);
    }


    @Override
    protected void handlePongMessage(
            WebSocketSession session, PongMessage message) throws Exception {
    }

}
