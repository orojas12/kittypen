import { test, expect } from "vitest";
import AppMessageBinaryEncoder, {
  ACTION_HEADER_BYTES,
  CHANNEL_HEADER_BYTES,
  EPOCH_MILLISECOND_BYTES,
  PAYLOAD_HEADER_BYTES,
} from "../AppMessageBinaryEncoder";

test("encode", () => {
  const utf8Encoder = new TextEncoder();
  const timestamp = Date.now();
  const channel = "myChannel";
  const channelBytes = utf8Encoder.encode(channel);
  const action = "myAction";
  const actionBytes = utf8Encoder.encode(action);
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const encoder = new AppMessageBinaryEncoder();

  const result = new DataView(
    encoder.encode({ timestamp, channel, action, payload }),
  );
  let bufPos = 0;

  // result has correct byte length
  expect(result.byteLength).toEqual(
    EPOCH_MILLISECOND_BYTES +
      CHANNEL_HEADER_BYTES +
      channelBytes.length +
      ACTION_HEADER_BYTES +
      actionBytes.length +
      PAYLOAD_HEADER_BYTES +
      payload.length,
  );

  expect(Number(result.getBigUint64(bufPos))).toEqual(timestamp);
  bufPos += 8;

  // channel header equals channel byte length
  expect(result.getInt8(bufPos)).toEqual(channelBytes.length);
  bufPos += 1;

  // channel bytes equals message channel
  for (let i = 0; i < channelBytes.length; i++) {
    expect(result.getUint8(bufPos++)).toEqual(channelBytes[i]);
  }

  // action header equals action byte length
  expect(result.getInt8(bufPos)).toEqual(actionBytes.length);
  bufPos += 1;

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
  const timestamp = Date.now();
  const channel = "myChannel";
  const action = "myAction";
  const payload = new Uint8Array([219, 109, 182, 219, 109, 182]);
  const binaryMessage = encoder.encode({ timestamp, channel, action, payload });

  const result = encoder.decode(binaryMessage);

  expect(result.timestamp).toEqual(timestamp);
  expect(result.channel).toEqual(channel);
  expect(result.action).toEqual(action);

  const resultPayload = new Uint8Array(result.payload);

  for (let i = 0; i < payload.length; i++) {
    expect(resultPayload[i]).toEqual(payload[i]);
  }
});
