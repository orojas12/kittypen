package dev.oscarrojas.drawandguess;

import dev.oscarrojas.drawandguess.io.Action;
import dev.oscarrojas.drawandguess.io.InboundMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class MessageDispatcherTest {

    @Test
    void handleInboundMessage_nullHandlerThrowsException() {

        // no handlers registered
        MessageDispatcher dispatcher = new MessageDispatcher();

        var message = new InboundMessage<>(Action.CREATE_USER, Instant.now(),
                "senderId", "payload");

        Assertions.assertThrows(IllegalStateException.class, () ->
                dispatcher.handleInboundMessage(message)
        );
    }

}
