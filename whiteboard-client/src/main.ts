import "./style.css";
import { Canvas } from "./canvas";

const canvasElement = document.getElementById("canvas") as HTMLCanvasElement;

if (canvasElement === null) {
  throw Error("Canvas is null");
}

const canvas = new Canvas(canvasElement, undefined, {
  width: 100,
  height: 100,
});

const resetBtn = document.getElementById("reset") as HTMLButtonElement;
