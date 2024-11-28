import "./style.css";

const canvas = document.getElementById("canvas") as HTMLCanvasElement;

if (canvas === null) {
  throw Error("Canvas is null");
}

const ctx = canvas.getContext("2d") as CanvasRenderingContext2D;

const resetBtn = document.getElementById("reset") as HTMLButtonElement;

const ws = new WebSocket("ws://localhost:8080");
