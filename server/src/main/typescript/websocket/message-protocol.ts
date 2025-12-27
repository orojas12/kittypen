import type { ProtocolMessage } from "@/types";
import { getPayloadType, PayloadType } from "@/PayloadType";
import { Action, getAction } from "@/Action";

const ACTION_SIZE_BYTE_LENGTH = 1; // byte
const PAYLOAD_TYPE_BYTE_LENGTH = 1; // byte
const PAYLOAD_SIZE_BYTE_LENGTH = 4; // int
const TIMESTAMP_BYTE_LENGTH = 8; // long

const utf8Encoder = new TextEncoder();
const utf8Decoder = new TextDecoder();

export function serialize(message: ProtocolMessage): Uint8Array {
  const action: Uint8Array = utf8Encoder.encode(message.action.toString());
  let payload: Uint8Array;
  const payloadType: PayloadType =
    message.payload instanceof Uint8Array
      ? PayloadType.BINARY
      : PayloadType.JSON;

  if (payloadType === PayloadType.BINARY) {
    payload = message.payload as Uint8Array;
  } else {
    payload = utf8Encoder.encode(JSON.stringify(message.payload));
  }

  const messageBytes: Uint8Array = new Uint8Array(
    TIMESTAMP_BYTE_LENGTH +
      ACTION_SIZE_BYTE_LENGTH +
      action.byteLength +
      PAYLOAD_TYPE_BYTE_LENGTH +
      PAYLOAD_SIZE_BYTE_LENGTH +
      payload.byteLength,
  );

  const view = new DataView(messageBytes.buffer);
  let bytePos = 0;

  // write timestamp bytes
  view.setBigUint64(bytePos, BigInt(message.timestamp.getTime()));
  bytePos += 8;

  // write action size
  view.setUint8(bytePos++, action.byteLength);

  // write action bytes
  messageBytes.set(action, bytePos);
  bytePos += action.byteLength;

  // write payload type
  view.setUint8(bytePos++, payloadType);

  // write payload size
  view.setUint32(bytePos, payload.byteLength);
  bytePos += 4;

  // write payload bytes
  messageBytes.set(payload, bytePos);

  return messageBytes;
}

export function deserialize(message: Uint8Array): ProtocolMessage {
  const view = new DataView(message.buffer);
  let bytePos = 0;

  // read timestamp
  const timestamp: Date = new Date(Number(view.getBigUint64(bytePos)));
  bytePos += 8;

  // read action
  const actionSize = view.getUint8(bytePos++);
  const action: Action = getAction(
    utf8Decoder.decode(message.slice(bytePos, bytePos + actionSize)),
  );
  bytePos += actionSize;

  // read payload
  const payloadType: PayloadType = getPayloadType(view.getUint8(bytePos++));
  const payloadSize = view.getUint32(bytePos);
  bytePos += 4;

  let payload: Uint8Array | {};
  if (payloadType === PayloadType.BINARY) {
    payload = message.slice(bytePos, bytePos + payloadSize);
  } else {
    payload = JSON.parse(
      utf8Decoder.decode(message.slice(bytePos, bytePos + payloadSize)),
    );
  }

  return {
    timestamp,
    action,
    payload,
  };
}
