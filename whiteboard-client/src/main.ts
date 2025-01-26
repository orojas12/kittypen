import "./style.css";
import { Canvas } from "./canvas/canvas";

const canvasElement = document.getElementById("canvas") as HTMLCanvasElement;

if (canvasElement === null) {
  throw Error("Canvas is null");
}

const ctx = canvasElement.getContext("2d") as CanvasRenderingContext2D;

const canvas = new Canvas(ctx, { width: 10, height: 10, lineWidth: 1 });

const resetBtn = document.getElementById("reset") as HTMLButtonElement;
