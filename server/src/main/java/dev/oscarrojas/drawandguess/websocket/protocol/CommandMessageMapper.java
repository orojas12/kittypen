package dev.oscarrojas.drawandguess.websocket.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

public class CommandMessageMapper {

    public static final int TIMESTAMP_HEADER_BYTES = 1;
    public static final int COMMAND_HEADER_BYTES = 1;
    public static final int MAX_COMMAND_BYTES = (2 ^ (COMMAND_HEADER_BYTES * 8));
    public static final int PAYLOAD_HEADER_BYTES = 4;
//
//    private final ObjectMapper mapper;
//
//    public CommandMessageMapper(ObjectMapper mapper) {
//        this.mapper = mapper;
//    }
//
//    public byte[] toBytes(CommandMessage<byte[]> request) {
//        byte[] timestampBytes = request.getTimestamp().toString()
//            .getBytes(StandardCharsets.UTF_8);
//        byte[] commandBytes = request.getCommand().getBytes(StandardCharsets.UTF_8);
//        byte[] payloadBytes = request.getPayload();
//
//        byte[] bytes = new byte[
//            TIMESTAMP_HEADER_BYTES
//                + timestampBytes.length
//                + COMMAND_HEADER_BYTES
//                + commandBytes.length
//                + PAYLOAD_HEADER_BYTES
//                + payloadBytes.length
//            ];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//
//        // write timestamp header
//        buffer.put((byte) timestampBytes.length);
//
//        // write timestamp
//        buffer.put(timestampBytes);
//
//        // write command header
//        buffer.put((byte) commandBytes.length);
//
//        // write command bytes
//        buffer.put(commandBytes);
//
//        // write payload header
//        buffer.putInt(payloadBytes.length);
//
//        // write payload bytes
//        buffer.put(payloadBytes);
//
//        return buffer.array();
//    }
//
//    public CommandMessage<byte[]> fromBytes(ByteBuffer buffer) {
//        if (buffer.position() != 0) {
//            buffer.rewind();
//        }
//
//        CommandMessage<byte[]> message = new CommandMessage<>();
//
//        try {
//
//            // extract timestamp header
//            int timestampLength = buffer.get() & 0xFF;
//
//            // extract timestamp
//            byte[] timestampBytes = new byte[timestampLength];
//            for (int i = 0; i < timestampLength; i++) {
//                timestampBytes[i] = buffer.get();
//            }
//            message.setTimestamp(
//                Instant.parse(new String(timestampBytes, StandardCharsets.UTF_8)));
//
//            // extract command header
//            int commandLength = buffer.get() & 0xFF;
//
//            // extract command
//            byte[] command = new byte[commandLength];
//            for (int i = 0; i < commandLength; i++) {
//                command[i] = buffer.get();
//            }
//            message.setCommand(new String(command, StandardCharsets.UTF_8));
//
//            // extract payload header
//            int payloadLength = buffer.getInt();
//
//            // ensure valid input buffer length
//            if (TIMESTAMP_HEADER_BYTES
//                + timestampLength
//                + COMMAND_HEADER_BYTES
//                + commandLength
//                + PAYLOAD_HEADER_BYTES
//                + payloadLength
//                != buffer.capacity()) {
//                throw new BinaryConverterException(
//                    "Unexpected buffer length (headers do not match actual buffer length)");
//            }
//
//            // extract payload
//            byte[] payload = new byte[payloadLength];
//            for (int i = 0; i < payloadLength; i++) {
//                payload[i] = buffer.get();
//            }
//            message.setPayload(payload);
//
//        } catch (IndexOutOfBoundsException e) {
//            throw new BinaryConverterException(
//                "Unexpected end of buffer: \n" + e.getMessage());
//        }
//
//        return message;
//
//    }
//
//    public CommandMessage<byte[]> fromBytes(byte[] bytes) {
//        return fromBytes(ByteBuffer.wrap(bytes));
//    }
//
//    public String toJson(
//        CommandMessage<?> message
//    ) throws JsonProcessingException {
//        return mapper.writeValueAsString(message);
//    }
//
//    public CommandMessage<Map<String, Object>> fromJson(
//        String json
//    ) throws JsonProcessingException {
//        return mapper.readValue(
//            json, new TypeReference<>() {
//            }
//        );
//    }

}
