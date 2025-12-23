package dev.oscarrojas.drawandguess.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.io.Action;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProtocolMessageSerializerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProtocolMessageSerializer serializer = new ProtocolMessageSerializer(mapper);

    @Test
    void serializeAndDeserialize_BinaryPayload_ReturnsOriginalMessage() throws IOException {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        byte[] originalPayload = new byte[]{0, 1, 2};
        ProtocolMessage<byte[]> originalMessage = new ProtocolMessage<>(timestamp,
                Action.CREATE_USER, originalPayload);

        byte[] serialized = serializer.serialize(originalMessage);
        @SuppressWarnings("unchecked")
        ProtocolMessage<byte[]> deserialized =
                (ProtocolMessage<byte[]>) serializer.deserialize(serialized);

        assertEquals(originalMessage.timestamp(), deserialized.timestamp());
        assertEquals(originalMessage.action(), deserialized.action());
        assertArrayEquals(originalPayload, deserialized.payload());
    }

    @Test
    void serialize_ProducesExpectedFormat() throws JsonProcessingException {
        long epochMilli = 1_735_732_801_500L; // Jan 1, 2025, 12:00:01.500 UTC
        Instant timestamp = Instant.ofEpochMilli(epochMilli);
        byte[] payload = new byte[]{0, 1, 2};
        ProtocolMessage<byte[]> message = new ProtocolMessage<>(timestamp,
                Action.CREATE_USER, payload
        );
        byte[] result = serializer.serialize(message);

        byte[] expectedFormat = new byte[]{
                0, 0, 1, -108, 33, -68, -81, -36, // Epoch milliseconds (long)
                11,                         // Action string length (byte)
                67, 82, 69, 65, 84, 69, 95, 85, 83, 69, 82, // "CREATE_USER"
                0,                          // Payload type (byte)
                0, 0, 0, 3,                 // Payload size (int)
                0, 1, 2                     // Payload data
        };

        assertArrayEquals(expectedFormat, result);
    }

}
