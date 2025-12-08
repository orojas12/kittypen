package dev.oscarrojas.drawandguess;

import dev.oscarrojas.drawandguess.io.InboundMessage;
import dev.oscarrojas.drawandguess.io.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class DrawAndGuessControllerTest {

    @Test
    void handleInboundMessage_nullHandlerThrowsException() {

        // no handlers registered
        DrawAndGuessController app = new DrawAndGuessController();

        var message = new InboundMessage<>(MessageType.NEW_USER, Instant.now(),
                "senderId", "payload");

        Assertions.assertThrows(IllegalStateException.class, () ->
                app.handleInboundMessage(message)
        );
    }

}
