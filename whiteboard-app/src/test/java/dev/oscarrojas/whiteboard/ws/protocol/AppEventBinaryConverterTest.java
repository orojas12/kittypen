package dev.oscarrojas.whiteboard.ws.protocol;

import dev.oscarrojas.whiteboard.messaging.AppEvent;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppEventBinaryConverterTest {

    @Test
    void toBytes() {
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
        AppEventBinaryConverter converter = new AppEventBinaryConverter();
        AppEvent event = new AppEvent(name, payload);

        byte[] result = converter.toBytes(event);
        ByteBuffer buffer = ByteBuffer.wrap(result);

        for (byte b : result) {
            System.out.print(b + " ");
        }

        // epoch milliseconds equals timestamp epoch milliseconds
        long milliseconds = buffer.getLong();
        assertEquals(milliseconds, event.getTimestamp().toEpochMilli());

        // name header equals name byte length
        int nameByteLength = buffer.get() & 0xFF;
        assertEquals(
            name.getBytes(StandardCharsets.UTF_8).length, nameByteLength);

        // name bytes equals event name
        byte[] nameBytes = new byte[nameByteLength];
        buffer.get(nameBytes, 0, nameByteLength);
        assertEquals(name, new String(nameBytes, StandardCharsets.UTF_8));

        // payload header equals payload byte length
        int payloadByteLength = buffer.getInt();
        assertEquals(payload.length, payloadByteLength);

        // payload bytes equals message payload
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
        AppEventBinaryConverter converter = new AppEventBinaryConverter();
        AppEvent event = new AppEvent(name, payload);
        byte[] eventBytes = converter.toBytes(event);

        AppEvent result = converter.fromBytes(eventBytes);

        // decoded timestamp equals original timestamp
        assertEquals(event.getTimestamp(), result.getTimestamp());

        // decoded name equals original name
        assertEquals(name, result.getName());

        // decoded payload equals original payload
        byte[] decodedPayload = result.getPayload();
        for (int i = 0; i < payload.length; i++) {
            assertEquals(payload[i], decodedPayload[i]);
        }
    }
}
