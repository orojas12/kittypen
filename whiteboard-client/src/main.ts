import "./style.css";
import { Canvas } from "./canvas";

const canvasElement = document.getElementById("canvas") as HTMLCanvasElement;

if (canvasElement === null) {
  throw Error("Canvas is null");
}

canvasElement.width = 4;
canvasElement.height = 4;

const ctx = canvasElement.getContext("2d") as CanvasRenderingContext2D;
ctx.imageSmoothingEnabled = false;

const canvas = new Canvas(ctx);

const line = {
  startX: 0,
  startY: 0,
  endX: 15,
  endY: 15,
  rgba: {
    r: 255,
    g: 0,
    b: 0,
    a: 255,
  },
};

const resetBtn = document.getElementById("reset") as HTMLButtonElement;
