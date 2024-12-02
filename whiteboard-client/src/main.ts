import "./style.css";
import { Canvas } from "./canvas";

const canvasElement = document.getElementById("canvas") as HTMLCanvasElement;

if (canvasElement === null) {
  throw Error("Canvas is null");
}

const ctx = canvasElement.getContext("2d") as CanvasRenderingContext2D;

const canvas = new Canvas(ctx);

const resetBtn = document.getElementById("reset") as HTMLButtonElement;

const ws = new WebSocket("ws://localhost:8080");
