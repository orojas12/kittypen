package dev.oscarrojas.drawandguess.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.drawandguess.io.Action;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class ProtocolMessageSerializer {
    private static final int MAX_PAYLOAD_BYTE_LENGTH = 64000000;

    public static final int ACTION_SIZE_BYTE_LENGTH = Byte.BYTES;
    public static final int PAYLOAD_TYPE_BYTE_LENGTH = Byte.BYTES;
    public static final int PAYLOAD_SIZE_BYTE_LENGTH = Integer.BYTES;
    public static final int TIMESTAMP_BYTE_LENGTH = Long.BYTES;

    private final ObjectMapper objectMapper;

    public ProtocolMessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] serialize(ProtocolMessage<?> message) throws JsonProcessingException {
        byte[] action = message.action().toString().getBytes(StandardCharsets.UTF_8);
        PayloadType payloadType = message.payload() instanceof byte[] ? PayloadType.BINARY : PayloadType.JSON;

        byte[] payload;
        if (payloadType == PayloadType.BINARY) {
            payload = (byte[]) message.payload();
        } else {
            payload = objectMapper.writeValueAsBytes(message.payload());
        }

        byte[] messageBytes = new byte
                [TIMESTAMP_BYTE_LENGTH
                        + ACTION_SIZE_BYTE_LENGTH
                        + action.length
                        + PAYLOAD_TYPE_BYTE_LENGTH
                        + PAYLOAD_SIZE_BYTE_LENGTH
                        + payload.length];

        ByteBuffer buffer = ByteBuffer.wrap(messageBytes);
        writeTimestamp(message.timestamp(), buffer);
        writeAction(message.action(), buffer);
        writePayload(payload, payloadType, buffer);

        return buffer.array();
    }

    public ProtocolMessage<?> deserialize(byte[] message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message);

        Instant timestamp = readTimestamp(buffer);
        Action action = readAction(buffer);
        PayloadType payloadType = readPayloadType(buffer);
        byte[] payload = readPayload(buffer);

        if (payloadType == PayloadType.BINARY) {
            return new ProtocolMessage<>(timestamp, action, payload);
        } else {
            return new ProtocolMessage<>(timestamp, action, objectMapper.readValue(payload, action.payloadType));
        }
    }

    private void writeTimestamp(Instant timestamp, ByteBuffer buffer) {
        buffer.putLong(timestamp.toEpochMilli());
    }

    private Instant readTimestamp(ByteBuffer buffer) {
        return Instant.ofEpochMilli(buffer.getLong());
    }

    private void writeAction(Action action, ByteBuffer buffer) {
        // write action size
        byte[] bytes = action.toString().getBytes(StandardCharsets.UTF_8);
        buffer.put((byte) bytes.length);

        // write action
        buffer.put(bytes);
    }

    private Action readAction(ByteBuffer buffer) {
        int actionSize = Byte.toUnsignedInt(buffer.get());
        byte[] actionBytes = new byte[actionSize];
        buffer.get(actionBytes, 0, actionSize);
        return Action.valueOf(new String(actionBytes, StandardCharsets.UTF_8));
    }

    private void writePayload(byte[] payload, PayloadType type, ByteBuffer buffer) throws JsonProcessingException {
        // write payload type
        buffer.put((byte) type.value);

        // write payload size
        buffer.putInt(payload.length);

        // write payload
        buffer.put(payload);
    }

    private PayloadType readPayloadType(ByteBuffer buffer) {
        return PayloadType.fromValue(Byte.toUnsignedInt(buffer.get()));
    }

    private byte[] readPayload(ByteBuffer buffer) {
        int payloadSize = buffer.getInt();
        byte[] payload = new byte[payloadSize];
        buffer.get(payload, 0, payloadSize);
        return payload;
    }
}
