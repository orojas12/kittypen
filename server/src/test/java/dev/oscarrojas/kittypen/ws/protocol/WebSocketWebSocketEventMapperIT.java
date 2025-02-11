package dev.oscarrojas.kittypen.ws.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WebSocketWebSocketEventMapperIT {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void toBytes() {
        Instant timestamp = Instant.now();
        String name = "eventName";

        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;
        WebSocketEventMapper mapper = new WebSocketEventMapper(objectMapper);
        WebSocketEvent<byte[]> webSocketEvent = new WebSocketEvent<>(timestamp, name, payload);

        byte[] result = mapper.toBytes(webSocketEvent);
        ByteBuffer buffer = ByteBuffer.wrap(result);

        // result timestamp header == timestamp byte length
        int timestampHeaderValue = buffer.get() & 0xFF;
        assertEquals(
            timestamp.toString().getBytes(StandardCharsets.UTF_8).length,
            timestampHeaderValue
        );

        // result timestamp == event timestamp
        byte[] timestampBytes = new byte[timestampHeaderValue];
        buffer.get(timestampBytes, 0, timestampHeaderValue);
        assertEquals(timestamp, Instant.parse(new String(timestampBytes, StandardCharsets.UTF_8)));

        // result name header == event name byte length
        int nameHeaderValue = buffer.get() & 0xFF;
        assertEquals(
            name.getBytes(StandardCharsets.UTF_8).length, nameHeaderValue);

        // result name == event name
        byte[] nameBytes = new byte[nameHeaderValue];
        buffer.get(nameBytes, 0, nameHeaderValue);
        assertEquals(name, new String(nameBytes, StandardCharsets.UTF_8));

        // result payload header == event payload byte length
        int payloadByteLength = buffer.getInt();
        assertEquals(payload.length, payloadByteLength);

        // result payload == event payload
        byte[] resultPayload = new byte[payload.length];
        buffer.get(resultPayload, 0, payload.length);
        for (int i = 0; i < payload.length; i++) {
            assertEquals(payload[i], resultPayload[i]);
        }

        // ensure no remaining bytes are present
        assertEquals(0, buffer.remaining());
    }

    @Test
    void fromBytes() throws BinaryDecodingException {
        Instant timestamp = Instant.now();
        String name = "eventName";
        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;
        WebSocketEventMapper mapper = new WebSocketEventMapper(objectMapper);
        WebSocketEvent<byte[]> webSocketEvent = new WebSocketEvent<>(timestamp, name, payload);
        byte[] eventBytes = mapper.toBytes(webSocketEvent);

        WebSocketEvent<byte[]> result = mapper.fromBytes(eventBytes);

        // decoded timestamp == original timestamp
        assertEquals(webSocketEvent.getTimestamp(), result.getTimestamp());

        // decoded name == original name
        assertEquals(name, result.getName());

        // decoded payload == original payload
        byte[] decodedPayload = result.getPayload();
        for (int i = 0; i < payload.length; i++) {
            assertEquals(payload[i], decodedPayload[i]);
        }
    }
}
