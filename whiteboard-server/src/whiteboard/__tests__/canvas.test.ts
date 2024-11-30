import { test, expect } from "vitest";
import { Canvas } from "../canvas";

test("draws line", () => {
  const canvas = new Canvas(3, 3);
  const rgba = { r: 1, g: 2, b: 3, a: 4 };
  canvas.drawLine(0, 0, 9, 9, rgba);
  const data = canvas.getData();

  // expect a line to be drawn from (0, 0) to (3, 3) in a 3x3 grid
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
