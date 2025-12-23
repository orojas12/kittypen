package dev.oscarrojas.drawandguess;

import dev.oscarrojas.drawandguess.core.lobby.Lobby;
import dev.oscarrojas.drawandguess.handlers.MessageHandler;
import dev.oscarrojas.drawandguess.io.Action;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.OutboundMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    private final List<Lobby> lobbies;
    private final Map<Action, MessageHandler<?, ?>> messageHandlers;

    public MessageDispatcher() {
        lobbies = new ArrayList<>();
        messageHandlers = new HashMap<>();
    }

    public <I, O> OutboundMessage<O> handleInboundMessage(InboundMessage<I> message) {

        @SuppressWarnings("unchecked")
        MessageHandler<I, O> handler = (MessageHandler<I, O>) messageHandlers.get(message.action());

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for message action: " + message.action()
            );
        }

        return handler.handleMessage(message);
    }

    public void register(Action action, MessageHandler<?, ?> handler) {
        messageHandlers.put(action, handler);
    }

}
