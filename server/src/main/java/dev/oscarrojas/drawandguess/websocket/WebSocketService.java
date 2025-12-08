package dev.oscarrojas.drawandguess.websocket;

import dev.oscarrojas.drawandguess.DrawAndGuessController;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Controller
public class WebSocketService extends AbstractWebSocketHandler {

    private DrawAndGuessController controller;

    public WebSocketService(DrawAndGuessController controller) {
        this.controller = controller;
    }


    @Override
    protected void handleTextMessage(
        WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    protected void handleBinaryMessage(
        WebSocketSession session, BinaryMessage message) throws Exception {

    }


    @Override
    protected void handlePongMessage(
        WebSocketSession session, PongMessage message) throws Exception {
    }

}
