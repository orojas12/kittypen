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

    // RFC 3339: 'YYYY-MM-DDTHH:MM:SS.SSSZ' (UTC)
    public static final int TIMESTAMP_BYTE_LENGTH = Long.BYTES + Integer.BYTES;

    private final ObjectMapper objectMapper;

    public ProtocolMessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] serialize(ProtocolMessage<?> message) throws JsonProcessingException {
        byte[] action = message.action().toString().getBytes(StandardCharsets.UTF_8);
        PayloadType payloadType = message.payload() instanceof byte[] ? PayloadType.BINARY :
                PayloadType.JSON;
        byte[] payload;

        if (payloadType == PayloadType.BINARY) {
            payload = (byte[]) message.payload();
        } else {
            payload = objectMapper.writeValueAsBytes(message.payload());
        }

        byte[] messageBytes = new byte[
                TIMESTAMP_BYTE_LENGTH
                        + ACTION_SIZE_BYTE_LENGTH
                        + action.length
                        + PAYLOAD_TYPE_BYTE_LENGTH
                        + PAYLOAD_SIZE_BYTE_LENGTH
                        + payload.length
                ];

        ByteBuffer buffer = ByteBuffer.wrap(messageBytes);
        writeTimestamp(message.timestamp(), buffer);
        writeAction(message.action(), buffer);
        writePayload(payload, payloadType, buffer);

        return buffer.array();
    }

    public ProtocolMessage<?> deserialize(byte[] message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message);

        // extract timestamp header
        int timestampLength = buffer.get() & 0xFF;

        // extract timestamp
        byte[] timestamp = new byte[timestampLength];
        for (int i = 0; i < timestampLength; i++) {
            timestamp[i] = buffer.get();
        }

        // extract action header
        int typeLength = buffer.get() & 0xFF;

        // extract action
        byte[] actionBytes = new byte[typeLength];
        for (int i = 0; i < typeLength; i++) {
            actionBytes[i] = buffer.get();
        }

        // extract payload header
        int payloadType = buffer.get() & 0xFF;
        int payloadLength = buffer.getInt();

        // extract payload
        byte[] payload = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++) {
            payload[i] = buffer.get();
        }

        Action action = Action.valueOf(new String(actionBytes, StandardCharsets.UTF_8));

        if (payloadType == PayloadType.BINARY.value) {
            return new ProtocolMessage<>(
                    Instant.parse(new String(timestamp, StandardCharsets.UTF_8)),
                    action,
                    payload
            );
        } else if (payloadType == PayloadType.JSON.value) {
            return new ProtocolMessage<>(
                    Instant.parse(new String(timestamp, StandardCharsets.UTF_8)),
                    action,
                    objectMapper.readValue(payload, action.payloadType)
            );
        }

        return null;
    }

    void writeTimestamp(Instant timestamp, ByteBuffer buffer) {
        long epochSeconds = timestamp.getEpochSecond();
        int epochNano = timestamp.getNano();
        buffer.putLong(epochSeconds);
        buffer.putInt(epochNano);
    }

    void writeAction(Action action, ByteBuffer buffer) {
        // write action size
        byte[] bytes = action.toString().getBytes(StandardCharsets.UTF_8);
        buffer.put((byte) bytes.length);

        // write action
        buffer.put(bytes);
    }

    void writePayload(byte[] payload, PayloadType type, ByteBuffer buffer) throws JsonProcessingException {
        // write payload type
        buffer.put((byte) type.value);

        // write payload size
        buffer.putInt(payload.length);

        // write payload
        buffer.put(payload);
    }
}
