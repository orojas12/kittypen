package dev.oscarrojas.drawandguess.handlers;

import dev.oscarrojas.drawandguess.dto.CreateLobby;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.OutboundMessage;

public class NewUserHandler implements MessageHandler<CreateLobby, CreateLobby> {

    @Override
    public OutboundMessage<CreateLobby> handleMessage(InboundMessage<CreateLobby> inboundMessage) {
        CreateLobby user = inboundMessage.payload();
        System.out.println(user.username());
        return null;
    }

}
