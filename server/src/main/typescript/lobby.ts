import { Canvas } from "@/canvas/canvas";
import { LobbyClient } from "@/lobby/lobby-client";

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

const client = new LobbyClient(
  `ws://${window.location.host + window.location.pathname}/ws`,
);
