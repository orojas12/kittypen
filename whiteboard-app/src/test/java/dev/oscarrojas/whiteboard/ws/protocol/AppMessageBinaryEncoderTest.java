package dev.oscarrojas.whiteboard.ws.protocol;

import dev.oscarrojas.whiteboard.messaging.AppMessage;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppMessageBinaryEncoderTest {

    @Test
    void encode() {
        String channel = "myChannel";
        String action = "myAction";

        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;
        AppMessageBinaryEncoder encoder = new AppMessageBinaryEncoder();
        AppMessage message = new AppMessage(channel, action, payload);

        byte[] result = encoder.encode(message);
        ByteBuffer buffer = ByteBuffer.wrap(result);

        for (byte b : result) {
            System.out.print(b + " ");
        }

        // epoch milliseconds equals timestamp epoch milliseconds
        long milliseconds = buffer.getLong();
        assertEquals(milliseconds, message.getTimestamp().toEpochMilli());

        // channel header equals channel byte length
        int channelByteLength = buffer.get() & 0xFF;
        assertEquals(
            channel.getBytes(StandardCharsets.UTF_8).length, channelByteLength);

        // channel bytes equals message channel
        byte[] channelBytes = new byte[channelByteLength];
        buffer.get(channelBytes, 0, channelByteLength);
        assertEquals(channel, new String(channelBytes, StandardCharsets.UTF_8));

        // action header equals action byte length
        int actionByteLength = buffer.get() & 0xFF;
        assertEquals(
            action.getBytes(StandardCharsets.UTF_8).length, actionByteLength);

        // action bytes equals message action
        byte[] actionBytes = new byte[actionByteLength];
        buffer.get(actionBytes, 0, actionByteLength);
        assertEquals(action, new String(actionBytes, StandardCharsets.UTF_8));

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
    void decode() throws BinaryDecodingException {
        String channel = "myChannel";
        String action = "myAction";
        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;
        AppMessageBinaryEncoder encoder = new AppMessageBinaryEncoder();
        AppMessage message = new AppMessage(channel, action, payload);
        byte[] encodedMsg = encoder.encode(message);

        AppMessage decodedMsg = encoder.decode(encodedMsg);

        // decoded timestamp equals original timestamp
        assertEquals(message.getTimestamp(), decodedMsg.getTimestamp());

        // decoded channel equals original channel
        assertEquals(channel, decodedMsg.getChannel());

        // decoded action equals original action
        assertEquals(action, decodedMsg.getAction());

        // decoded payload equals original payload
        byte[] decodedPayload = decodedMsg.getPayload();
        for (int i = 0; i < payload.length; i++) {
            assertEquals(payload[i], decodedPayload[i]);
        }
    }
}
