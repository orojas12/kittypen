import { test, expect } from "vitest";
import AppEventBinaryConverter, {
  NAME_HEADER_BYTES,
  EPOCH_MILLISECOND_BYTES,
  PAYLOAD_HEADER_BYTES,
} from "../AppEventBinaryConverter";

test("converts AppEvent to bytes", () => {
  const utf8Encoder = new TextEncoder();
  const timestamp = Date.now();
  const name = "eventName";
  const nameBytes = utf8Encoder.encode(name);
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const converter = new AppEventBinaryConverter();

  const result = new DataView(converter.toBytes({ timestamp, name, payload }));
  let bufPos = 0;

  // result has correct byte length
  expect(result.byteLength).toEqual(
    EPOCH_MILLISECOND_BYTES +
      NAME_HEADER_BYTES +
      nameBytes.length +
      PAYLOAD_HEADER_BYTES +
      payload.length,
  );

  expect(Number(result.getBigUint64(bufPos))).toEqual(timestamp);
  bufPos += 8;

  // name header equals name byte length
  expect(result.getInt8(bufPos)).toEqual(nameBytes.length);
  bufPos += 1;

  // name bytes equals event name
  for (let i = 0; i < nameBytes.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(nameBytes[i]);
  }

  // payload header equals payload byte length
  expect(result.getUint32(bufPos)).toEqual(payload.length);
  bufPos += 4;

  // payload bytes equals message payload
  for (let i = 0; i < payload.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(payload[i]);
  }
});

test("converts event bytes to AppEvent", () => {
  const converter = new AppEventBinaryConverter();
  const timestamp = Date.now();
  const name = "eventName";
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const binaryMessage = converter.toBytes({
    timestamp,
    name,
    payload,
  });

  const result = converter.fromBytes(binaryMessage);

  expect(result.timestamp).toEqual(timestamp);
  expect(result.name).toEqual(name);

  const resultPayload = new Uint8Array(result.payload);

  for (let i = 0; i < payload.length; i++) {
    expect(resultPayload[i]).toEqual(payload[i]);
  }
});
