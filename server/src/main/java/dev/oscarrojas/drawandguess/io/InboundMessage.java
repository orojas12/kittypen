package dev.oscarrojas.drawandguess.io;

import java.time.Instant;

public record InboundMessage<T>(MessageType type, Instant timestamp,
                                String senderId, T payload) {}
