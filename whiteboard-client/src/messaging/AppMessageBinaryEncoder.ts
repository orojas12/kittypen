import type { AppMessage } from "./types";

export const EPOCH_MILLISECOND_BYTES = 8;
export const CHANNEL_HEADER_BYTES = 1;
export const ACTION_HEADER_BYTES = 1;
export const PAYLOAD_HEADER_BYTES = 4;

export default class AppMessageBinaryEncoder {
  utf8Encoder = new TextEncoder();
  utf8Decoder = new TextDecoder();

  encode = (message: AppMessage): ArrayBuffer => {
    const channelBytes: Uint8Array = this.utf8Encoder.encode(message.channel);
    const actionBytes: Uint8Array = this.utf8Encoder.encode(message.action);
    const payloadBytes = new Uint8Array(message.payload);

    // create buffer
    const view = new DataView(
      new ArrayBuffer(
        EPOCH_MILLISECOND_BYTES +
          CHANNEL_HEADER_BYTES +
          channelBytes.length +
          ACTION_HEADER_BYTES +
          actionBytes.length +
          PAYLOAD_HEADER_BYTES +
          payloadBytes.length,
      ),
    );

    let bytePos = 0;

    // write timestamp
    view.setBigUint64(bytePos, BigInt(message.timestamp));
    bytePos += 8;

    // write channel header
    view.setUint8(bytePos++, channelBytes.length);

    // write channel bytes
    for (let i = 0; i < channelBytes.length; i++) {
      view.setUint8(bytePos++, channelBytes[i]);
    }

    // write action header
    view.setUint8(bytePos++, actionBytes.length);

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

    // extract timestamp
    message.timestamp = Number(view.getBigUint64(bufPos));
    bufPos += 8;

    // extract channel header
    const channelByteLength = view.getUint8(bufPos++);

    // extract channel
    const channelBytes = new Uint8Array(channelByteLength);
    for (let i = 0; i < channelByteLength; i++) {
      channelBytes[i] = view.getUint8(bufPos++);
    }
    message.channel = this.utf8Decoder.decode(channelBytes);

    // extract action header
    const actionByteLength = view.getUint8(bufPos++);

    // extract action
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
    message.payload = payload.buffer;

    return message;
  };
}
