package dev.oscarrojas.kittypen.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.oscarrojas.kittypen.core.command.CommandMessage;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandMessageMapperTest {

    @Test
    void toBytes() {
        String command = "commandName";

        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;

        CommandMessageMapper converter = new CommandMessageMapper(
            new ObjectMapper());
        CommandMessage<byte[]> message = new CommandMessage<>(
            Instant.now(),
            command, payload
        );

        byte[] result = converter.toBytes(message);
        ByteBuffer buffer = ByteBuffer.wrap(result);

        for (byte b : result) {
            System.out.print(b + " ");
        }

        // timestamp header equals timestamp byte length
        byte timestampByteLength = buffer.get();
        byte[] timestampBytes = message.getTimestamp().toString().getBytes(StandardCharsets.UTF_8);
        assertEquals(timestampBytes.length, timestampByteLength);

        // result timestamp equals original timestamp
        byte[] resultTimestampBytes = new byte[timestampByteLength];
        buffer.get(resultTimestampBytes, 0, timestampByteLength);
        assertEquals(
            message.getTimestamp(),
            Instant.parse(new String(
                resultTimestampBytes,
                StandardCharsets.UTF_8
            ))
        );

        // command header equals command byte length
        int commandByteLength = buffer.get() & 0xFF;
        assertEquals(
            command.getBytes(StandardCharsets.UTF_8).length, commandByteLength);

        // command bytes equals request command
        byte[] nameBytes = new byte[commandByteLength];
        buffer.get(nameBytes, 0, commandByteLength);
        assertEquals(command, new String(nameBytes, StandardCharsets.UTF_8));

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
    void fromBytes() throws BinaryConverterException {
        String command = "commandName";
        byte[] payload = new byte[8];
        payload[0] = (byte) 219;
        payload[1] = (byte) 109;
        payload[2] = (byte) 182;
        payload[3] = (byte) 219;
        payload[4] = (byte) 219;
        payload[5] = (byte) 109;
        payload[6] = (byte) 182;
        payload[7] = (byte) 219;
        CommandMessageMapper converter = new CommandMessageMapper(
            new ObjectMapper());
        CommandMessage<byte[]> request =
            new CommandMessage<>(
                Instant.now(),
                command,
                payload
            );
        byte[] bytes = converter.toBytes(request);

        CommandMessage<byte[]> result = converter.fromBytes(bytes);

        // decoded timestamp equals original timestamp
        assertEquals(request.getTimestamp(), result.getTimestamp());

        // decoded command equals original command
        assertEquals(command, result.getCommand());

        // decoded payload equals original payload
        byte[] decodedPayload = result.getPayload();
        for (int i = 0; i < payload.length; i++) {
            assertEquals(payload[i], decodedPayload[i]);
        }
    }

    @Test
    void toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        CommandMessageMapper mapper = new CommandMessageMapper(objectMapper);
        CommandMessage<User> message = new CommandMessage<>(
            Instant.now(),
            "command",
            new User("user")
        );
        String json = mapper.toJson(message);
        assertEquals(
            "{\"timestamp\":\"" + message.getTimestamp().toString() + "\"," +
                "\"command\":\"command\"," +
                "\"payload\":{\"name\":\"user\"}}",
            json
        );
    }

    @Test
    void fromJson() throws JsonProcessingException {
        WebSocketRequest<User> request = new WebSocketRequest<>(
            Instant.now(),
            "command",
            new User("user")
        );
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = objectMapper.writeValueAsString(request);
        CommandMessageMapper mapper = new CommandMessageMapper(objectMapper);
        CommandMessage<Map<String, Object>> result = mapper.fromJson(json);

        assertEquals(request.getTimestamp(), result.getTimestamp());
        assertEquals(request.getCommand(), result.getCommand());
        assertEquals(request.getPayload().getName(), result.getPayload().get("name"));
    }

    static class User {

        private String name;

        User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}
