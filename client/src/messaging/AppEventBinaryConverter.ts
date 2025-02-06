import type { AppEvent } from "./types";

export const EPOCH_MILLISECOND_BYTES = 8;
export const NAME_HEADER_BYTES = 1;
export const PAYLOAD_HEADER_BYTES = 4;

export default class AppEventBinaryConverter {
  utf8Encoder = new TextEncoder();
  utf8Decoder = new TextDecoder();

  toBytes = (event: AppEvent): ArrayBuffer => {
    const nameBytes: Uint8Array = this.utf8Encoder.encode(event.name);
    const payloadBytes = new Uint8Array(event.payload);

    // create buffer
    const view = new DataView(
      new ArrayBuffer(
        EPOCH_MILLISECOND_BYTES +
          NAME_HEADER_BYTES +
          nameBytes.length +
          PAYLOAD_HEADER_BYTES +
          payloadBytes.length,
      ),
    );

    let bytePos = 0;

    // write timestamp
    view.setBigUint64(bytePos, BigInt(event.timestamp));
    bytePos += 8;

    // write channel header
    view.setUint8(bytePos++, nameBytes.length);

    // write channel bytes
    for (let i = 0; i < nameBytes.length; i++) {
      view.setUint8(bytePos++, nameBytes[i]);
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

  fromBytes = (bytes: ArrayBuffer): AppEvent => {
    const view = new DataView(bytes);
    const event = {} as AppEvent;
    let bufPos = 0;

    // extract timestamp
    event.timestamp = Number(view.getBigUint64(bufPos));
    bufPos += 8;

    // extract channel header
    const nameByteLength = view.getUint8(bufPos++);

    // extract channel
    const nameBytes = new Uint8Array(nameByteLength);
    for (let i = 0; i < nameByteLength; i++) {
      nameBytes[i] = view.getUint8(bufPos++);
    }
    event.name = this.utf8Decoder.decode(nameBytes);

    // extract payload header
    const payloadByteLength = view.getUint32(bufPos);
    bufPos += 4;

    // extract payload
    const payload = new Uint8Array(payloadByteLength);
    for (let i = 0; i < payloadByteLength; i++) {
      payload[i] = view.getUint8(bufPos++);
    }
    event.payload = payload.buffer;

    return event;
  };
}
