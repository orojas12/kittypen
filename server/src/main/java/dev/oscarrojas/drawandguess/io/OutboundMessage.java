package dev.oscarrojas.drawandguess.io;

import java.time.Instant;

public record OutboundMessage<T>(MessageType type, Instant timestamp,
                                 String recipientId, T payload){}
