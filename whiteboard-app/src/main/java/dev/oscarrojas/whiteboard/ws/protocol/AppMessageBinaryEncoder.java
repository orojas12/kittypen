package dev.oscarrojas.whiteboard.ws.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
public class AppMessageBinaryEncoder {

  private static final int EVENT_LENGTH_HEADER_BYTES = 2;
  private static final int PAYLOAD_LENGTH_HEADER_BYTES = 4;

  public byte[] encode(AppMessage message) {
    String event = message.getEvent();
    byte[] payload = message.getPayload();

    byte[] eventBytes = event.getBytes(StandardCharsets.UTF_8);
    byte[] buffer = new byte[2 + eventBytes.length + 4 + payload.length];

    // write event length header
    buffer[0] = (byte) ((eventBytes.length & 0xFF00) >>> 8);
    buffer[1] = (byte) ((eventBytes.length & 0xFF));

    // write event bytes
    for (int i = 0; i < eventBytes.length; i++) {
      buffer[i + EVENT_LENGTH_HEADER_BYTES] = eventBytes[i];
    }

    // write payload length header
    buffer[EVENT_LENGTH_HEADER_BYTES + eventBytes.length] = (byte) ((payload.length & 0xFF000000) >>> 24);
    buffer[EVENT_LENGTH_HEADER_BYTES + eventBytes.length + 1] = (byte) ((payload.length & 0xFF0000) >>> 16);
    buffer[EVENT_LENGTH_HEADER_BYTES + eventBytes.length + 2] = (byte) ((payload.length & 0xFF00) >>> 8);
    buffer[EVENT_LENGTH_HEADER_BYTES + eventBytes.length + 3] = (byte) (payload.length & 0xFF);

    // write payload bytes
    for (int i = 0; i < payload.length; i++) {
      buffer[i + EVENT_LENGTH_HEADER_BYTES + eventBytes.length + PAYLOAD_LENGTH_HEADER_BYTES] = payload[i];
    }

    return buffer;
  }

  public AppMessage decode(byte[] bytes) throws BinaryDecodingException {
    AppMessage message = new AppMessage();

    try {

      int pos = 0;

      // extract event length header
      short eventLengthMsb = (short) ((bytes[pos++] & 0xFFFF) << 8);
      short eventLengthLsb = (short) ((bytes[pos++] & 0xFFFF));
      short eventLength = (short) (eventLengthMsb | eventLengthLsb);

      // extract event
      byte[] event = new byte[eventLength];
      for (int i = 0; i < eventLength; i++) {
        event[i] = bytes[pos++];
      }
      message.setEvent(new String(event, StandardCharsets.UTF_8));

      // extract payload length header
      int payloadLength1 = (bytes[pos++] & 0xFFFFFFFF) << 24;
      int payloadLength2 = (bytes[pos++] & 0xFFFFFFFF) << 16;
      int payloadLength3 = (bytes[pos++] & 0xFFFFFFFF) << 8;
      int payloadLength4 = (bytes[pos++] & 0xFFFFFFFF);
      int payloadLength = payloadLength1 | payloadLength2 | payloadLength3 | payloadLength4;

      // ensure valid input buffer length
      if (EVENT_LENGTH_HEADER_BYTES + eventLength + PAYLOAD_LENGTH_HEADER_BYTES + payloadLength != bytes.length) {
        throw new BinaryDecodingException(
            "Unexpected buffer length (event/payload headers do not match actual event/payload length)");
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
