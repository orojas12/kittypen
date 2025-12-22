package dev.oscarrojas.drawandguess.io;

import java.time.Instant;

public record OutboundMessage<T>(Action action, Instant timestamp,
                                 String recipientId, T payload){}
