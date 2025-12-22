package dev.oscarrojas.drawandguess.io;

import java.time.Instant;
import java.util.List;

public record BroadcastMessage<T>(Action action, Instant timestamp,
                                  List<String> recipientIds, T payload) {}