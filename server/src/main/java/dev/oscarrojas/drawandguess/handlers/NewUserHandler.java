package dev.oscarrojas.drawandguess.handlers;

import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.OutboundMessage;
import dev.oscarrojas.drawandguess.dto.NewUser;

public class NewUserHandler implements MessageHandler<NewUser, NewUser> {

    @Override
    public OutboundMessage<NewUser> handleMessage(InboundMessage<NewUser> inboundMessage) {
        NewUser user = inboundMessage.payload();
        System.out.println(user.username());
        return null;
    }

}
