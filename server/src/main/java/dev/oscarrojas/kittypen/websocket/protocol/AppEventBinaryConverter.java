package dev.oscarrojas.kittypen.websocket.protocol;

import dev.oscarrojas.kittypen.messaging.BinaryAppEvent;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class AppEventBinaryConverter {

    private static final int EPOCH_MILLISECOND_BYTES = 8;
    private static final int NAME_HEADER_BYTES = 1;
    private static final int PAYLOAD_HEADER_BYTES = 4;
    public static final int BASE_FRAME_SIZE =
        EPOCH_MILLISECOND_BYTES
            + NAME_HEADER_BYTES
            + (2 ^ (NAME_HEADER_BYTES * 8))
            + PAYLOAD_HEADER_BYTES;

    public byte[] toBytes(BinaryAppEvent event) {
        String name = event.getName();
        byte[] payload = event.getPayload();

        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[
            EPOCH_MILLISECOND_BYTES
                + NAME_HEADER_BYTES
                + nameBytes.length
                + PAYLOAD_HEADER_BYTES
                + payload.length
            ];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // write timestamp (epoch milliseconds)
        buffer.putLong(event.getTimestamp().toEpochMilli());

        // write name header
        buffer.put((byte) nameBytes.length);

        // write name bytes
        buffer.put(nameBytes);

        // write payload header
        buffer.putInt(payload.length);

        // write payload bytes
        buffer.put(payload);

        return buffer.array();
    }

    public BinaryAppEvent fromBytes(ByteBuffer buffer) throws BinaryDecodingException {
        if (buffer.position() != 0) {
            buffer.rewind();
        }

        BinaryAppEvent event = new BinaryAppEvent();

        try {

            // extract timestamp
            event.setTimestamp(Instant.ofEpochMilli(buffer.getLong()));

            // extract name header
            int nameLength = buffer.get() & 0xFF;

            // extract name
            byte[] name = new byte[nameLength];
            for (int i = 0; i < nameLength; i++) {
                name[i] = buffer.get();
            }
            event.setName(new String(name, StandardCharsets.UTF_8));

            // extract payload header
            int payloadLength = buffer.getInt();

            // ensure valid input buffer length
            if (EPOCH_MILLISECOND_BYTES
                + NAME_HEADER_BYTES
                + nameLength
                + PAYLOAD_HEADER_BYTES
                + payloadLength
                != buffer.capacity()) {
                throw new BinaryDecodingException(
                    "Unexpected buffer length (headers do not match actual buffer length)");
            }

            // extract payload
            byte[] payload = new byte[payloadLength];
            for (int i = 0; i < payloadLength; i++) {
                payload[i] = buffer.get();
            }
            event.setPayload(payload);

        } catch (IndexOutOfBoundsException e) {
            throw new BinaryDecodingException(
                "Unexpected end of buffer: \n" + e.getMessage());
        }

        return event;
    }

    public BinaryAppEvent fromBytes(byte[] bytes) throws BinaryDecodingException {
        return fromBytes(ByteBuffer.wrap(bytes));
    }
}
