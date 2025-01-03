package dev.oscarrojas.whiteboard.ws.protocol;

import dev.oscarrojas.whiteboard.ws.AppMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class AppMessageBinaryEncoder {

  private static final int CHANNEL_HEADER_BYTES = 2;
  private static final int ACTION_HEADER_BYTES = 2;
  private static final int PAYLOAD_HEADER_BYTES = 4;

  public byte[] encode(AppMessage message) {
    String channel = message.getChannel();
    String action = message.getAction();
    byte[] payload = message.getPayload();

    byte[] channelBytes = channel.getBytes(StandardCharsets.UTF_8);
    byte[] actionBytes = action.getBytes(StandardCharsets.UTF_8);
    byte[] buffer =
        new byte
            [CHANNEL_HEADER_BYTES
                + channelBytes.length
                + ACTION_HEADER_BYTES
                + actionBytes.length
                + PAYLOAD_HEADER_BYTES
                + payload.length];

    // write channel header (2 bytes)
    int bufPos = 0;
    buffer[bufPos++] = (byte) ((channelBytes.length & 0xFF00) >>> 8);
    buffer[bufPos++] = (byte) ((channelBytes.length & 0xFF));

    // write channel bytes
    for (int i = 0; i < channelBytes.length; i++) {
      buffer[bufPos++] = channelBytes[i];
    }

    // write action header (2 bytes)
    buffer[bufPos++] = (byte) ((actionBytes.length & 0xFF00) >>> 8);
    buffer[bufPos++] = (byte) ((actionBytes.length & 0xFF));

    // write action bytes
    for (int i = 0; i < actionBytes.length; i++) {
      buffer[bufPos++] = actionBytes[i];
    }

    // write payload header (4 bytes)
    buffer[bufPos++] = (byte) ((payload.length & 0xFF000000) >>> 24);
    buffer[bufPos++] = (byte) ((payload.length & 0xFF0000) >>> 16);
    buffer[bufPos++] = (byte) ((payload.length & 0xFF00) >>> 8);
    buffer[bufPos++] = (byte) (payload.length & 0xFF);

    // write payload bytes
    for (int i = 0; i < payload.length; i++) {
      buffer[bufPos++] = payload[i];
    }

    return buffer;
  }

  public AppMessage decode(byte[] bytes) throws BinaryDecodingException {
    AppMessage message = new AppMessage();

    try {

      int pos = 0;

      // extract channel header
      short channelLengthMsb = (short) ((bytes[pos++] & 0xFFFF) << 8);
      short channelLengthLsb = (short) ((bytes[pos++] & 0xFFFF));
      short channelLength = (short) (channelLengthMsb | channelLengthLsb);

      // extract channel
      byte[] channel = new byte[channelLength];
      for (int i = 0; i < channelLength; i++) {
        channel[i] = bytes[pos++];
      }
      message.setChannel(new String(channel, StandardCharsets.UTF_8));

      // extract action header
      short actionLengthMsb = (short) ((bytes[pos++] & 0xFFFF) << 8);
      short actionLengthLsb = (short) ((bytes[pos++] & 0xFFFF));
      short actionLength = (short) (actionLengthMsb | actionLengthLsb);

      // extract action
      byte[] action = new byte[actionLength];
      for (int i = 0; i < actionLength; i++) {
        action[i] = bytes[pos++];
      }
      message.setAction(new String(action, StandardCharsets.UTF_8));

      // extract payload header
      int payloadLength1 = (bytes[pos++] & 0xFFFFFFFF) << 24;
      int payloadLength2 = (bytes[pos++] & 0xFFFFFFFF) << 16;
      int payloadLength3 = (bytes[pos++] & 0xFFFFFFFF) << 8;
      int payloadLength4 = (bytes[pos++] & 0xFFFFFFFF);
      int payloadLength = payloadLength1 | payloadLength2 | payloadLength3 | payloadLength4;

      // ensure valid input buffer length
      if (CHANNEL_HEADER_BYTES
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
        payload[i] = bytes[pos++];
      }
      message.setPayload(payload);

    } catch (IndexOutOfBoundsException exc) {
      throw new BinaryDecodingException("Unexpected end of buffer: \n" + exc.getMessage());
    }

    return message;
  }

  public AppMessage decode(ByteBuffer bytes) throws BinaryDecodingException {
    return decode(bytes.array());
  }
}
