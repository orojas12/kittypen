import type { AppMessage } from "./types";

export default class AppMessageBinaryEncoder {
  EVENT_HEADER_BYTE_LENGTH = 2;
  PAYLOAD_HEADER_BYTE_LENGTH = 4;

  utf8Encoder = new TextEncoder();
  utf8Decoder = new TextDecoder();

  encode = (message: AppMessage): ArrayBuffer => {
    // get event utf8 bytes
    const eventBytes: Uint8Array = this.utf8Encoder.encode(message.event);

    // create buffer
    const view = new DataView(
      new ArrayBuffer(
        this.EVENT_HEADER_BYTE_LENGTH +
          eventBytes.length +
          this.PAYLOAD_HEADER_BYTE_LENGTH +
          message.payload.length,
      ),
    );

    let bytePos = 0;

    // write event header
    view.setUint16(bytePos, eventBytes.length);
    bytePos += 2;

    // write event bytes
    for (let i = 0; i < eventBytes.length; i++) {
      view.setUint8(bytePos++, eventBytes[i]);
    }

    // write payload header
    view.setUint32(bytePos, message.payload.length);
    bytePos += 4;

    // write payload bytes
    for (let i = 0; i < message.payload.length; i++) {
      view.setUint8(bytePos++, message.payload[i]);
    }

    return view.buffer;
  };

  decode = (bytes: ArrayBuffer): AppMessage => {
    const view = new DataView(bytes);
    const message = {} as AppMessage;
    let bufPos = 0;

    // extract event header
    const eventByteLength = view.getInt16(bufPos);
    bufPos += 2;

    // extract event
    const eventBytes = new Uint8Array(eventByteLength);
    for (let i = 0; i < eventByteLength; i++) {
      eventBytes[i] = view.getUint8(bufPos++);
    }
    message.event = this.utf8Decoder.decode(eventBytes);

    // extract payload header
    const payloadByteLength = view.getUint32(bufPos);
    bufPos += 4;

    // extract payload
    const payload = new Uint8Array(payloadByteLength);
    for (let i = 0; i < payloadByteLength; i++) {
      payload[i] = view.getUint8(bufPos++);
    }
    message.payload = payload;

    return message;
  };
}
