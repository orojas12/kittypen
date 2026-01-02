package dev.oscarrojas.drawandguess.io;

import java.time.Instant;

public record InboundMessage<T>(Action action, Instant timestamp, String senderId, T payload) {}
