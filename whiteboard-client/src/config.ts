import EventEmitter from "./messaging/EventEmitter";
import CanvasFrameBinaryConverter from "./canvas/CanvasFrameBinaryConverter";
import { onCanvasPutFrame, onCanvasUpdate } from "./canvas/listeners";

const canvasFrameConverter = new CanvasFrameBinaryConverter();
const eventEmitter = new EventEmitter();

eventEmitter.on("canvas.update", onCanvasUpdate);
eventEmitter.on("canvas.putFrame", onCanvasPutFrame);

export { eventEmitter, canvasFrameConverter };
