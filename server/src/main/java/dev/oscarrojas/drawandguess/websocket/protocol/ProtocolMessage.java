package dev.oscarrojas.drawandguess.websocket.protocol;

import dev.oscarrojas.drawandguess.io.Action;

import java.time.Instant;

public record ProtocolMessage<T>(Instant timestamp, Action action, T payload) {
}
