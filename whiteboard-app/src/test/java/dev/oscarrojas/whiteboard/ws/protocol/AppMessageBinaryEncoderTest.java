package dev.oscarrojas.whiteboard.ws.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class AppMessageBinaryEncoderTest {

  @Test
  void encode() {
    String event = "myEvent";
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
    AppMessage message = new AppMessage(event, payload);

    byte[] result = encoder.encode(message);
    ByteBuffer buffer = ByteBuffer.wrap(result);

    for (int i = 0; i < result.length; i++) {
      System.out.print((int) (result[i] & 0xFF) + " ");
    }

    // event header equals event byte length
    short eventByteLength = buffer.getShort();
    assertEquals(event.getBytes(StandardCharsets.UTF_8).length, eventByteLength);

    // event bytes equals message event
    byte[] eventBytes = new byte[eventByteLength];
    buffer.get(eventBytes, 0, eventByteLength);
    assertEquals(event, new String(eventBytes, StandardCharsets.UTF_8));

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
    String event = "myEvent";
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
    byte[] encodedMsg = encoder.encode(new AppMessage(event, payload));

    AppMessage decodedMsg = encoder.decode(encodedMsg);

    // decoded event equals original event
    assertEquals(event, decodedMsg.getEvent());

    // decoded payload equals original payload
    byte[] decodedPayload = decodedMsg.getPayload();
    for (int i = 0; i < payload.length; i++) {
      assertEquals(payload[i], decodedPayload[i]);
    }
  }
}
