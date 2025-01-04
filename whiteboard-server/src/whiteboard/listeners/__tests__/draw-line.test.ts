import { test, expect } from "vitest";

import { canvasDataToBase64 } from "../draw-line";

test("encodes Uint8ClampedArray to base64", () => {
  const arr1 = new Uint8ClampedArray(1000 * 1000 * 4);
  for (let i = 0; i < arr1.length; i++) {
    arr1[i] = Math.floor(Math.random() * 100);
  }

  const base64 = Buffer.from(arr1).toString("base64");
  const result = canvasDataToBase64(arr1);
  expect(result).toEqual(base64);
});
