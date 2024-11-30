import { test, expect } from "vitest";
import { Canvas } from "../canvas";

test("draws line from (0,0) to (3,3) in 3x3 grid", () => {
  const canvas = new Canvas(3, 3);
  const rgba = { r: 1, g: 2, b: 3, a: 4 };
  canvas.drawLine(0, 0, 9, 9, rgba);
  const data = canvas.getData();

  for (let i = 0; i < data.length; i += 4) {
    const pixel = Math.floor(i / 4);
    if (pixel === 0 || pixel === 4 || pixel === 8) {
      // this pixel should be drawn
      expect(data[i]).toEqual(rgba.r);
      expect(data[i + 1]).toEqual(rgba.g);
      expect(data[i + 2]).toEqual(rgba.b);
      expect(data[i + 3]).toEqual(rgba.a);
    } else {
      // this pixel should not be drawn
      expect(data[i]).toEqual(0);
      expect(data[i + 1]).toEqual(0);
      expect(data[i + 2]).toEqual(0);
      expect(data[i + 3]).toEqual(0);
    }
  }
});

test("draws line from (1,1) to (4,3) in 4x4 grid", () => {
  const canvas = new Canvas(4, 4);
  const rgba = { r: 1, g: 2, b: 3, a: 4 };
  canvas.drawLine(1, 1, 4, 3, rgba);
  const data = canvas.getData();

  for (let i = 0; i < data.length; i += 4) {
    const pixel = Math.floor(i / 4);
    if (pixel === 5 || pixel === 10 || pixel === 11) {
      // this pixel should be drawn
      expect(data[i]).toEqual(rgba.r);
      expect(data[i + 1]).toEqual(rgba.g);
      expect(data[i + 2]).toEqual(rgba.b);
      expect(data[i + 3]).toEqual(rgba.a);
    } else {
      // this pixel should not be drawn
      expect(data[i]).toEqual(0);
      expect(data[i + 1]).toEqual(0);
      expect(data[i + 2]).toEqual(0);
      expect(data[i + 3]).toEqual(0);
    }
  }
});
