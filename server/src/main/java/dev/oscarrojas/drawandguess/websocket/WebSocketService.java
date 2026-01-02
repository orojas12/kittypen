package dev.oscarrojas.drawandguess.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.MessageDispatcher;
import dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessageSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketService extends TextWebSocketHandler {

    private final MessageDispatcher dispatcher;
    private final ProtocolMessageSerializer serializer;

    public WebSocketService(ObjectMapper objectMapper) {
        this.dispatcher = new MessageDispatcher();
        this.serializer = new ProtocolMessageSerializer(objectMapper);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New WebSocket connection: " + session.getId());
        session.sendMessage(new TextMessage("Hello there!"));
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws Exception {
        System.out.printf("Message from websocket %s: %s%n", session.getId(),
                message.toString());
    }

//    @Override
//    protected void handleBinaryMessage(
//            WebSocketSession session, BinaryMessage message) throws Exception {
//        ProtocolMessage<?> protocolMessage = serializer.deserialize(message.getPayload().array());
//        InboundMessage<?> inboundMessage = new InboundMessage<>(protocolMessage.action(),
//                protocolMessage.timestamp(), session.getId(), protocolMessage.payload());
//        dispatcher.handleInboundMessage(inboundMessage);
//    }
//
//
//    @Override
//    protected void handlePongMessage(
//            WebSocketSession session, PongMessage message) throws Exception {
//    }

}
