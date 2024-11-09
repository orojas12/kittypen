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

// fill canvas black
const imgData = ctx.getImageData(0, 0, ctx.canvas.width, ctx.canvas.height);

enum RgbaComponent {
  ALPHA = 0,
  RED = 1,
  GREEN = 2,
  BLUE = 3,
}

for (let i = 0; i < imgData.data.length; i++) {
  const rgbaComponent = (i + 1) % 4;
  if (rgbaComponent === RgbaComponent.RED) {
    imgData.data[i] = 255;
  } else if (rgbaComponent === RgbaComponent.ALPHA) {
    imgData.data[i] = 255;
  }
}

ctx.putImageData(imgData, 0, 0);

setInterval(() => {
  console.log(ctx.getImageData(0, 0, 100, 100));
}, 5000);
