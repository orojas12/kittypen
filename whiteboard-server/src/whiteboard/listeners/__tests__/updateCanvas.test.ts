import { test, expect } from "vitest";

import { base64ToCanvasData } from "../updateCanvas";

test("converts base64 to canvas data", () => {
  const arr = new Uint8ClampedArray(1000);
  for (let i = 0; i < arr.length; i++) {
    arr[i] = Math.floor(Math.random() * 256);
  }

  const base64 = Buffer.from(arr).toString("base64");

  const result = base64ToCanvasData(base64);

  expect(arr.length).toEqual(result.length);

  for (let i = 0; i < arr.length; i++) {
    expect(arr[i]).toEqual(result[i]);
  }
});
