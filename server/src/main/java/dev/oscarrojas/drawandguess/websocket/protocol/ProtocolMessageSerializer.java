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

        // read timestamp
        Instant timestamp = Instant.ofEpochMilli(buffer.getLong());

        // read action size
        int actionSize = Byte.toUnsignedInt(buffer.get());

        // read action string
        byte[] actionBytes = new byte[actionSize];
        buffer.get(actionBytes, 0, actionSize);
        Action action = Action.valueOf(new String(actionBytes, StandardCharsets.UTF_8));

        // read payload type
        PayloadType payloadType = PayloadType.fromValue(Byte.toUnsignedInt(buffer.get()));

        // read payload size
        int payloadSize = buffer.getInt();

        // read payload
        byte[] payload = new byte[payloadSize];
        buffer.get(payload, 0, payloadSize);


        if (payloadType == PayloadType.BINARY) {
            return new ProtocolMessage<>(
                    timestamp,
                    action,
                    payload
            );
        } else if (payloadType == PayloadType.JSON) {
            return new ProtocolMessage<>(
                    timestamp,
                    action,
                    objectMapper.readValue(payload, action.payloadType)
            );
        }

        return null;
    }

    private void writeTimestamp(Instant timestamp, ByteBuffer buffer) {
        buffer.putLong(timestamp.toEpochMilli());
    }

    private void writeAction(Action action, ByteBuffer buffer) {
        // write action size
        byte[] bytes = action.toString().getBytes(StandardCharsets.UTF_8);
        buffer.put((byte) bytes.length);

        // write action
        buffer.put(bytes);
    }

    private void writePayload(byte[] payload, PayloadType type, ByteBuffer buffer) throws JsonProcessingException {
        // write payload type
        buffer.put((byte) type.value);

        // write payload size
        buffer.putInt(payload.length);

        // write payload
        buffer.put(payload);
    }
}
