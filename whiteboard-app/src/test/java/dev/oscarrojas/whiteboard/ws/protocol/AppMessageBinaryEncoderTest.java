package dev.oscarrojas.whiteboard.ws.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.oscarrojas.whiteboard.ws.AppMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

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

    for (int i = 0; i < result.length; i++) {
      System.out.print((int) (result[i] & 0xFF) + " ");
    }

    // channel header equals channel byte length
    short channelByteLength = buffer.getShort();
    assertEquals(channel.getBytes(StandardCharsets.UTF_8).length, channelByteLength);

    // channel bytes equals message channel
    byte[] channelBytes = new byte[channelByteLength];
    buffer.get(channelBytes, 0, channelByteLength);
    assertEquals(channel, new String(channelBytes, StandardCharsets.UTF_8));

    // action header equals action byte length
    short actionByteLength = buffer.getShort();
    assertEquals(action.getBytes(StandardCharsets.UTF_8).length, actionByteLength);

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
    byte[] encodedMsg = encoder.encode(new AppMessage(channel, action, payload));

    AppMessage decodedMsg = encoder.decode(encodedMsg);

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
