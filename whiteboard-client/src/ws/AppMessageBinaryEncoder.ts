import type { AppMessage } from "./types";

export default class AppMessageBinaryEncoder {
  CHANNEL_HEADER_BYTE_LENGTH = 2;
  ACTION_HEADER_BYTE_LENGTH = 2;
  PAYLOAD_HEADER_BYTE_LENGTH = 4;

  utf8Encoder = new TextEncoder();
  utf8Decoder = new TextDecoder();

  encode = (message: AppMessage): ArrayBuffer => {
    // get event utf8 bytes
    const channelBytes: Uint8Array = this.utf8Encoder.encode(message.channel);
    const actionBytes: Uint8Array = this.utf8Encoder.encode(message.action);
    const payloadBytes = new Uint8Array(message.payload);

    // create buffer
    const view = new DataView(
      new ArrayBuffer(
        this.CHANNEL_HEADER_BYTE_LENGTH +
          channelBytes.length +
          this.ACTION_HEADER_BYTE_LENGTH +
          actionBytes.length +
          this.PAYLOAD_HEADER_BYTE_LENGTH +
          payloadBytes.length,
      ),
    );

    let bytePos = 0;

    // write channel header
    view.setUint16(bytePos, channelBytes.length);
    bytePos += 2;

    // write channel bytes
    for (let i = 0; i < channelBytes.length; i++) {
      view.setUint8(bytePos++, channelBytes[i]);
    }

    // write action header
    view.setUint16(bytePos, actionBytes.length);
    bytePos += 2;

    // write action bytes
    for (let i = 0; i < actionBytes.length; i++) {
      view.setUint8(bytePos++, actionBytes[i]);
    }

    // write payload header
    view.setUint32(bytePos, payloadBytes.length);
    bytePos += 4;

    // write payload bytes
    for (let i = 0; i < payloadBytes.length; i++) {
      view.setUint8(bytePos++, payloadBytes[i]);
    }

    return view.buffer;
  };

  decode = (bytes: ArrayBuffer): AppMessage => {
    const view = new DataView(bytes);
    const message = {} as AppMessage;
    let bufPos = 0;

    // extract channel header
    const channelByteLength = view.getInt16(bufPos);
    bufPos += 2;

    // extract channel
    const channelBytes = new Uint8Array(channelByteLength);
    for (let i = 0; i < channelByteLength; i++) {
      channelBytes[i] = view.getUint8(bufPos++);
    }
    message.channel = this.utf8Decoder.decode(channelBytes);

    // extract action header
    const actionByteLength = view.getInt16(bufPos);
    bufPos += 2;

    // extract channel
    const actionBytes = new Uint8Array(actionByteLength);
    for (let i = 0; i < actionByteLength; i++) {
      actionBytes[i] = view.getUint8(bufPos++);
    }
    message.action = this.utf8Decoder.decode(actionBytes);

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
