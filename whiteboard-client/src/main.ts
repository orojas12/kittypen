import "./style.css";
import { Canvas } from "./canvas/canvas";

const canvasElement1 = document.getElementById("canvas1") as HTMLCanvasElement;

if (canvasElement1 === null) {
  throw Error("Canvas is null");
}

const ctx1 = canvasElement1.getContext("2d") as CanvasRenderingContext2D;

const canvas1 = new Canvas(ctx1, {
  width: 1000,
  height: 1000,
  lineWidth: 4,
});

// const resetBtn = document.getElementById("reset") as HTMLButtonElement;
