import { test, expect } from "vitest";
import AppMessageBinaryEncoder from "../AppMessageBinaryEncoder";

test("encode", () => {
  const utf8Encoder = new TextEncoder();
  const channel = "myChannel";
  const channelBytes = utf8Encoder.encode(channel);
  const action = "myAction";
  const actionBytes = utf8Encoder.encode(action);
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const encoder = new AppMessageBinaryEncoder();

  const result = new DataView(encoder.encode({ channel, action, payload }));
  let bufPos = 0;

  // result has correct byte length
  expect(result.byteLength).toEqual(
    2 + channelBytes.length + 2 + actionBytes.length + 4 + payload.length,
  );

  // channel header equals channel byte length
  expect(result.getInt16(bufPos)).toEqual(channelBytes.length);
  bufPos += 2;

  // channel bytes equals message channel
  for (let i = 0; i < channelBytes.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(channelBytes[i]);
  }

  // action header equals action byte length
  expect(result.getInt16(bufPos)).toEqual(actionBytes.length);
  bufPos += 2;

  // action bytes equals message action
  for (let i = 0; i < actionBytes.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(actionBytes[i]);
  }
  // payload header equals payload byte length
  expect(result.getUint32(bufPos)).toEqual(payload.length);
  bufPos += 4;

  // payload bytes equals message payload
  for (let i = 0; i < payload.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(payload[i]);
  }
});

test("decode", () => {
  const encoder = new AppMessageBinaryEncoder();
  const channel = "myChannel";
  const action = "myAction";
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const binaryMessage = encoder.encode({ channel, action, payload });

  const result = encoder.decode(binaryMessage);

  expect(result.channel).toEqual(channel);
  expect(result.action).toEqual(action);

  const resultPayload = new Uint8Array(result.payload);

  for (let i = 0; i < payload.length; i++) {
    expect(resultPayload[i]).toEqual(payload[i]);
  }
});
