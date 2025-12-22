package dev.oscarrojas.drawandguess.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.io.Action;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static dev.oscarrojas.drawandguess.websocket.protocol.ProtocolMessageSerializer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProtocolMessageSerializerTest {

    @Test
    void serialize_resultHasCorrectLength() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMessageSerializer serializer = new ProtocolMessageSerializer(mapper);

        ProtocolMessage<byte[]> message = new ProtocolMessage<>(Instant.now(), Action.CREATE_USER,
                new byte[]{0, 1, 2});
        var action = message.action().toString().getBytes(StandardCharsets.UTF_8);
        var payload = message.payload();
        int expectedLength = TIMESTAMP_BYTE_LENGTH
                + ACTION_SIZE_BYTE_LENGTH
                + action.length
                + PAYLOAD_TYPE_BYTE_LENGTH
                + PAYLOAD_SIZE_BYTE_LENGTH
                + payload.length;
        byte[] result = serializer.serialize(message);

        assertEquals(expectedLength, result.length);
    }

    @Test
    void serialize_resultHasCorrectData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMessageSerializer serializer = new ProtocolMessageSerializer(mapper);

        ProtocolMessage<byte[]> message = new ProtocolMessage<>(Instant.now(), Action.CREATE_USER,
                new byte[]{0, 1, 2});
        var expectedTimestamp = message.timestamp();
        var expectedAction = message.action();
        var expectedPayload = message.payload();

        byte[] result = serializer.serialize(message);
        ByteBuffer buffer = ByteBuffer.wrap(result);

        // has correct timestamp
        long epochSeconds = buffer.getLong();
        int epochNano = buffer.getInt();
        Instant timestamp = Instant.ofEpochSecond(epochSeconds, epochNano);
        assertEquals(expectedTimestamp, timestamp);

        // has correct action
        int actionLength = Byte.toUnsignedInt(buffer.get()); // 0-255 ascii characters
        byte[] actionBytes = new byte[actionLength];
        buffer.get(actionBytes, 0, actionLength);
        Action action = Action.valueOf(new String(actionBytes, StandardCharsets.UTF_8));
        assertEquals(expectedAction, action);

        // has correct payload type
        int payloadType = Byte.toUnsignedInt(buffer.get());
        assertEquals(PayloadType.BINARY.value, payloadType);

        // has correct payload data
        int payloadLength = buffer.getInt();
        byte[] payload = new byte[payloadLength];
        buffer.get(payload, 0, payloadLength);
        for (int i = 0; i < expectedPayload.length; i++) {
            assertEquals(expectedPayload[i], payload[i]);
        }
    }


}
