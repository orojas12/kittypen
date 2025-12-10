package dev.oscarrojas.drawandguess;

import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.MessageType;
import dev.oscarrojas.drawandguess.io.OutboundMessage;
import dev.oscarrojas.drawandguess.core.lobby.Lobby;
import dev.oscarrojas.drawandguess.handlers.MessageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DrawAndGuessController {

    private final List<Lobby> lobbies;
    private final Map<MessageType, MessageHandler<?, ?>> messageHandlers;

    public DrawAndGuessController() {
        lobbies = new ArrayList<>();
        messageHandlers = new HashMap<>();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Hello world");
        return "index";
    }

    public <I, O> OutboundMessage<O> handleInboundMessage(InboundMessage<I> message) {

        @SuppressWarnings("unchecked")
        MessageHandler<I, O> handler = (MessageHandler<I, O>) messageHandlers.get(message.type());

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for message type: " + message.type()
            );
        }

        return handler.handleMessage(message);
    }

    public <I, O> void register(MessageType type, MessageHandler<I, O> handler) {

        messageHandlers.put(type, handler);
    }

}
