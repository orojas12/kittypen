import { expect, test } from "vitest";
import { ProtocolMessage } from "@/types";
import { Action } from "@/Action";
import { deserialize, serialize } from "@/websocket/message-protocol";

test("serialize() and deserialize() returns original message", () => {
  const message: ProtocolMessage = {
    timestamp: new Date(),
    action: Action.CREATE_LOBBY,
    payload: new Uint8Array([0, 1, 2]),
  };

  const serialized: Uint8Array = serialize(message);
  const deserialized: ProtocolMessage = deserialize(serialized);

  expect(deserialized).toEqual(message);
});

test("serialize() produces expected format", () => {
  const epochMilli = 1_735_732_801_500;
  const message: ProtocolMessage = {
    timestamp: new Date(epochMilli),
    action: Action.CREATE_LOBBY,
    payload: new Uint8Array([0, 1, 2]),
  };

  const result: Uint8Array = serialize(message);

  // prettier-ignore
  const expectedFormat: Uint8Array = new Uint8Array([
    0, 0, 1, -108, 33, -68, -81, -36, // Epoch milliseconds (long)
    12,                               // Action string length (byte)
    67, 82, 69, 65, 84, 69, 95, 76, 79, 66, 66, 89, // "CREATE_LOBBY"
    0,                                // Payload type (byte)
    0, 0, 0, 3,                       // Payload size (int)
    0, 1, 2,                          // Payload data
  ]);

  expect(result.length).toEqual(expectedFormat.length);
  expect(result.every((value, i) => value === expectedFormat[i])).toEqual(true);
});
