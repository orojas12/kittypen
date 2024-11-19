import "./style.css";
import { Whiteboard } from "./whiteboard/Whiteboard";

const canvas = document.getElementById("canvas") as HTMLCanvasElement;

if (canvas === null) {
  throw Error("Canvas is null");
}

const ctx = canvas.getContext("2d") as CanvasRenderingContext2D;

const board = new Whiteboard(ctx);

const resetBtn = document.getElementById("reset") as HTMLButtonElement;
resetBtn.addEventListener("click", board.reset);
