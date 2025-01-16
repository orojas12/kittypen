package dev.oscarrojas.whiteboard.ws.protocol;

import dev.oscarrojas.whiteboard.messaging.AppMessage;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class AppMessageBinaryEncoder {

    private static final int EPOCH_MILLISECOND_BYTES = 8;
    private static final int CHANNEL_HEADER_BYTES = 1;
    private static final int ACTION_HEADER_BYTES = 1;
    private static final int PAYLOAD_HEADER_BYTES = 4;

    public byte[] encode(AppMessage message) {
        String channel = message.getChannel();
        String action = message.getAction();
        byte[] payload = message.getPayload();

        byte[] channelBytes = channel.getBytes(StandardCharsets.UTF_8);
        byte[] actionBytes = action.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[
            EPOCH_MILLISECOND_BYTES
                + CHANNEL_HEADER_BYTES
                + channelBytes.length
                + ACTION_HEADER_BYTES
                + actionBytes.length
                + PAYLOAD_HEADER_BYTES
                + payload.length
            ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // write timestamp (epoch milliseconds)
        buffer.putLong(message.getTimestamp().toEpochMilli());

        // write channel header
        buffer.put((byte) channelBytes.length);

        // write channel bytes
        buffer.put(channelBytes);

        // write action header (2 bytes)
        buffer.put((byte) actionBytes.length);

        // write action bytes
        buffer.put(actionBytes);

        // write payload header
        buffer.putInt(payload.length);

        // write payload bytes
        buffer.put(payload);

        return buffer.array();
    }

    public AppMessage decode(byte[] bytes) throws BinaryDecodingException {
        AppMessage message = new AppMessage();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        try {

            int pos = 0;

            // extract timestamp
            message.setTimestamp(Instant.ofEpochMilli(buffer.getLong()));

            // extract channel header
            int channelLength = buffer.get() & 0xFF;

            // extract channel
            byte[] channel = new byte[channelLength];
            for (int i = 0; i < channelLength; i++) {
                channel[i] = buffer.get();
            }
            message.setChannel(new String(channel, StandardCharsets.UTF_8));

            // extract action header
            int actionLength = buffer.get() & 0xFF;

            // extract action
            byte[] action = new byte[actionLength];
            for (int i = 0; i < actionLength; i++) {
                action[i] = buffer.get();
            }
            message.setAction(new String(action, StandardCharsets.UTF_8));

            // extract payload header
            int payloadLength = buffer.getInt();

            // ensure valid input buffer length
            if (EPOCH_MILLISECOND_BYTES
                + CHANNEL_HEADER_BYTES
                + channelLength
                + ACTION_HEADER_BYTES
                + actionLength
                + PAYLOAD_HEADER_BYTES
                + payloadLength
                != bytes.length) {
                throw new BinaryDecodingException(
                    "Unexpected buffer length (headers do not match actual buffer length)");
            }

            // extract payload
            byte[] payload = new byte[payloadLength];
            for (int i = 0; i < payloadLength; i++) {
                payload[i] = buffer.get();
            }
            message.setPayload(payload);

        } catch (IndexOutOfBoundsException e) {
            throw new BinaryDecodingException(
                "Unexpected end of buffer: \n" + e.getMessage());
        }

        return message;
    }

    public AppMessage decode(ByteBuffer bytes) throws BinaryDecodingException {
        return decode(bytes.array());
    }
}
