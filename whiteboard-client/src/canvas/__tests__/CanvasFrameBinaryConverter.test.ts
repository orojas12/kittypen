import { test, expect } from "vitest";
import CanvasFrameBinaryConverter from "../CanvasFrameBinaryConverter";

import type { CanvasFrame } from "../types";

const testImageData = new Uint8ClampedArray([
  1, 1, 1, 255, 2, 2, 2, 255, 3, 3, 3, 255, 4, 4, 4, 255,
]);

function createTestBinaryFrame() {
  const view = new DataView(new ArrayBuffer(4 * 4 + 16));
  let pos = 0;

  view.setUint32(pos, 1);
  pos += 4;

  view.setUint32(pos, 2);
  pos += 4;

  view.setUint32(pos, 3);
  pos += 4;

  view.setUint32(pos, 4);
  pos += 4;

  for (const b of testImageData) {
    view.setUint8(pos++, b);
  }

  return view;
}

test("canvas frame is converted from bytes", () => {
  const view = createTestBinaryFrame();
  const converter = new CanvasFrameBinaryConverter();

  const frame = converter.fromBytes(view.buffer) as CanvasFrame;

  expect(frame.startX).toEqual(1);
  expect(frame.startY).toEqual(2);
  expect(frame.endX).toEqual(3);
  expect(frame.endY).toEqual(4);

  for (let i = 0; i < testImageData.length; i++) {
    expect(frame.data[i]).toEqual(testImageData[i]);
  }
});

test("canvas frame is converted to bytes", () => {
  const view = createTestBinaryFrame();
  const converter = new CanvasFrameBinaryConverter();
  const frame = {
    startX: 1,
    startY: 2,
    endX: 3,
    endY: 4,
    data: testImageData,
  };

  const bytes = converter.toBytes(frame);

  const result = new DataView(bytes);
  let pos = 0;

  expect(result.getUint32(pos)).toEqual(1);
  pos += 4;

  expect(result.getUint32(pos)).toEqual(2);
  pos += 4;

  expect(result.getUint32(pos)).toEqual(3);
  pos += 4;

  expect(result.getUint32(pos)).toEqual(4);
  pos += 4;

  for (const b of testImageData) {
    expect(result.getUint8(pos++)).toEqual(b);
  }
});
