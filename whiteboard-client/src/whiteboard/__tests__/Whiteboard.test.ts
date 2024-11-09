import { describe, test, expect } from "vitest";
import { Whiteboard } from "../Whiteboard";

describe("Whiteboard", () => {
  const canvas = new HTMLCanvasElement();
  canvas.style.width = "405";
  canvas.style.height = "720";
  const ctx = canvas.getContext("2d") as CanvasRenderingContext2D;

  test("transforms position according to transformation scale", () => {
    const wb = new Whiteboard(ctx);
  });
});
