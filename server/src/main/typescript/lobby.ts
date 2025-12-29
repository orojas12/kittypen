import { Canvas } from "@/canvas/canvas";

const canvasElement = document.getElementById(
  "canvas",
) as HTMLCanvasElement | null;

if (canvasElement === null) {
  throw Error("No canvas element found");
}

const context = canvasElement.getContext("2d", {
  willReadFrequently: true,
}) as CanvasRenderingContext2D;

const canvas = new Canvas(context, {
  width: 1000,
  height: 1000,
  lineWidth: 4,
});
