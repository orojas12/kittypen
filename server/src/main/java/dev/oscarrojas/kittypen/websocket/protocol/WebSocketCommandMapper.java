package dev.oscarrojas.kittypen.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@Component
public class WebSocketCommandMapper {

    private static final int TIMESTAMP_HEADER_BYTES = 1;
    private static final int NAME_HEADER_BYTES = 1;
    private static final int PAYLOAD_HEADER_BYTES = 4;

    private ObjectMapper jsonMapper;

    public WebSocketCommandMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }

    public byte[] toBytes(WebSocketCommand<byte[]> webSocketEvent) {
        byte[] timestampBytes = webSocketEvent.getTimestamp().toString()
            .getBytes(StandardCharsets.UTF_8);
        byte[] nameBytes = webSocketEvent.getName().getBytes(StandardCharsets.UTF_8);
        byte[] payloadBytes = webSocketEvent.getPayload();

        byte[] eventBytes = new byte[
            TIMESTAMP_HEADER_BYTES
                + timestampBytes.length
                + NAME_HEADER_BYTES
                + nameBytes.length
                + PAYLOAD_HEADER_BYTES
                + payloadBytes.length
            ];
        ByteBuffer buffer = ByteBuffer.wrap(eventBytes);

        // write timestamp header
        buffer.put((byte) timestampBytes.length);

        // write timestamp
        buffer.put(timestampBytes);

        // write name header
        buffer.put((byte) nameBytes.length);

        // write name bytes
        buffer.put(nameBytes);

        // write payload header
        buffer.putInt(payloadBytes.length);

        // write payload bytes
        buffer.put(payloadBytes);

        return buffer.array();
    }

    public WebSocketCommand<byte[]> fromBytes(ByteBuffer buffer) {
        if (buffer.position() != 0) {
            buffer.rewind();
        }

        WebSocketCommand<byte[]> webSocketEvent = new WebSocketCommand<>();

        try {

            // extract timestamp header
            int timestampLength = buffer.get() & 0xFF;

            // extract timestamp
            byte[] timestampBytes = new byte[timestampLength];
            for (int i = 0; i < timestampLength; i++) {
                timestampBytes[i] = buffer.get();
            }
            webSocketEvent.setTimestamp(
                Instant.parse(new String(timestampBytes, StandardCharsets.UTF_8)));

            // extract name header
            int nameLength = buffer.get() & 0xFF;

            // extract name
            byte[] name = new byte[nameLength];
            for (int i = 0; i < nameLength; i++) {
                name[i] = buffer.get();
            }
            webSocketEvent.setName(new String(name, StandardCharsets.UTF_8));

            // extract payload header
            int payloadLength = buffer.getInt();

            // ensure valid input buffer length
            if (TIMESTAMP_HEADER_BYTES
                + timestampLength
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
            webSocketEvent.setPayload(payload);

        } catch (IndexOutOfBoundsException e) {
            throw new BinaryDecodingException(
                "Unexpected end of buffer: \n" + e.getMessage());
        }

        return webSocketEvent;

    }

    public WebSocketCommand<byte[]> fromBytes(byte[] bytes) {
        return fromBytes(ByteBuffer.wrap(bytes));
    }

    public String toJson(
        WebSocketCommand<Map<String, Object>> webSocketEvent
    ) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(webSocketEvent);
    }

    public WebSocketCommand<Map<String, Object>> fromJson(
        String json
    ) throws JsonProcessingException {
        return (WebSocketCommand<Map<String, Object>>) jsonMapper.readValue(
            json, WebSocketCommand.class);
    }

}
