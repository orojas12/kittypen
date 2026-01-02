package dev.oscarrojas.drawandguess.handlers;

import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.OutboundMessage;

public interface MessageHandler<I, O> {

    OutboundMessage<O> handleMessage(InboundMessage<I> inboundMessage);
}
