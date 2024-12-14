import { test, expect } from "vitest";

import { Canvas } from "../canvas";

test("decodes base64 correctly", () => {
  const canvas = new Canvas();

  const arr1 = new Uint8ClampedArray(1000);
  for (let i = 0; i < arr1.length; i++) {
    arr1[i] = Math.floor(Math.random() * 100);
  }

  const base64 = Buffer.from(arr1).toString("base64");

  const arr2 = canvas.base64ToUint8ClampedArray(base64);

  expect(arr1.length).toEqual(arr2.length);
  for (let i = 0; i < arr1.length; i++) {
    expect(arr1[i]).toEqual(arr2[i]);
  }
});
