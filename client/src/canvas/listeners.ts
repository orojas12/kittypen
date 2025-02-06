import type { EventListener } from "../types";

import { canvasFrameConverter } from "../config";
import { CanvasFrame } from "./types";

const onCanvasUpdate: EventListener<CanvasFrame> = (event, client): void => {
  const frameBytes = canvasFrameConverter.toBytes(event.payload);
  client.send({ ...event, name: "canvas.putFrame", payload: frameBytes });
};

const onCanvasPutFrame: EventListener<ArrayBuffer> = (event, client): void => {
  const { canvas } = client;
  const frame = canvasFrameConverter.fromBytes(event.payload);
  canvas.putFrame(frame);
};

export { onCanvasUpdate, onCanvasPutFrame };
