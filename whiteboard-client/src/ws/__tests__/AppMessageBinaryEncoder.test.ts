import { test, expect } from "vitest";
import AppMessageBinaryEncoder from "../AppMessageBinaryEncoder";

test("encode", () => {
  const utf8Encoder = new TextEncoder();
  const event = "myEvent";
  const eventBytes = utf8Encoder.encode(event);
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const encoder = new AppMessageBinaryEncoder();

  const result = new DataView(encoder.encode({ event, payload }));
  let bufPos = 0;

  // result has correct byte length
  expect(result.byteLength).toEqual(2 + eventBytes.length + 4 + payload.length);

  // event header equals event byte length
  expect(result.getInt16(bufPos)).toEqual(eventBytes.length);

  // event bytes equals message event
  bufPos += 2;
  for (let i = 0; i < eventBytes.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(eventBytes[i]);
  }

  // payload header equals payload byte length
  expect(result.getUint32(bufPos)).toEqual(payload.length);

  // payload bytes equals message payload
  bufPos += 4;
  for (let i = 0; i < payload.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(payload[i]);
  }
});

test("decode", () => {
  const encoder = new AppMessageBinaryEncoder();
  const event = "myEvent";
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const binaryMessage = encoder.encode({ event, payload });

  const result = encoder.decode(binaryMessage);

  expect(result.event).toEqual(event);

  for (let i = 0; i < payload.length; i++) {
    expect(result.payload[i]).toEqual(payload[i]);
  }
});
