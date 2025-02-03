import AppEventBinaryConverter from "../messaging/AppEventBinaryConverter";
import { AppEvent } from "../messaging/types";
import CanvasFrameBinaryConverter from "./CanvasFrameBinaryConverter";

export type CanvasOptions = {
  width: number;
  height: number;
  lineWidth: number;
  offline: boolean;
};

export type CanvasPointer = {
  x: number;
  y: number;
  prevX: number;
  prevY: number;
  isPressed: boolean;
};

type Coord = {
  x: number;
  y: number;
};

const DEFAULT_CANVAS_OPTIONS = {
  width: 100,
  height: 100,
  lineWidth: 4,
  offline: false,
};

export class Canvas {
  private options: CanvasOptions;
  private ctx: CanvasRenderingContext2D;
  private ws: WebSocket;
  private syncInterval: NodeJS.Timeout;
  private pointer: CanvasPointer;
  private messageEncoder: AppEventBinaryConverter;
  private canvasFrameConverter: CanvasFrameBinaryConverter;
  private drawBuffer: Coord[];

  constructor(ctx: CanvasRenderingContext2D, options?: Partial<CanvasOptions>) {
    this.messageEncoder = new AppEventBinaryConverter();
    this.canvasFrameConverter = new CanvasFrameBinaryConverter();
    this.options = {
      ...DEFAULT_CANVAS_OPTIONS,
      ...options,
    };
    this.pointer = {
      x: 0,
      y: 0,
      prevX: 0,
      prevY: 0,
      isPressed: false,
    };

    this.drawBuffer = [];

    this.ctx = this.setUpContext(ctx);

    !this.options.offline ? (this.ws = this.establishServerConnection()) : null;

    this.setUpEventListeners();

    requestAnimationFrame(this.updateFrame);

    this.syncInterval = setInterval(this.pushToServer, 100);
  }

  setUpContext = (ctx: CanvasRenderingContext2D): CanvasRenderingContext2D => {
    ctx.imageSmoothingEnabled = false;
    ctx.canvas.width = this.options.width;
    ctx.canvas.height = this.options.height;
    ctx.lineWidth = this.options.lineWidth;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    return ctx;
  };

  establishServerConnection = (): WebSocket => {
    const ws = new WebSocket("ws://localhost:8080");
    ws.binaryType = "arraybuffer";

    ws.addEventListener("open", (event) => {
      console.log("Connection established with ws://localhost:8080");
    });

    ws.addEventListener("message", (event) => {
      const message = this.messageEncoder.decode(event.data) as AppEvent;
      console.log(
        `received message: {\ntimestamp: ${message.timestamp} \nchannel: ${message.channel}, \naction: ${message.action}, \npayload: ${message.payload}`,
      );

      const canvasFrame = this.canvasFrameConverter.fromBytes(message.payload);

      const imgData = new ImageData(
        new Uint8ClampedArray(canvasFrame.data),
        canvasFrame.endX - canvasFrame.startX || 1,
        canvasFrame.endY - canvasFrame.startY || 1,
      );

      this.ctx.putImageData(imgData, canvasFrame.startX, canvasFrame.startY);
    });

    return ws;
  };

  setUpEventListeners = (): void => {
    document.addEventListener("pointerdown", this.handlePointerEvent);
    document.addEventListener("pointerup", this.handlePointerEvent);
    document.addEventListener("pointermove", this.handlePointerEvent);
  };

  handlePointerEvent = (event: PointerEvent): void => {
    const domRect = this.ctx.canvas.getBoundingClientRect();

    if (event.type === "pointerdown") {
      this.pointer.isPressed = true;
    } else if (event.type === "pointerup") {
      this.pointer.isPressed = false;
    } else {
      this.pointer.prevX = this.pointer.x;
      this.pointer.prevY = this.pointer.y;

      this.pointer.x = event.pageX - domRect.left;
      this.pointer.y = event.pageY - domRect.top;

      const scaleX = this.pointer.x / domRect.width;
      const scaleY = this.pointer.y / domRect.height;

      this.pointer.x = Math.trunc(this.options.width * scaleX) + 0.5;
      this.pointer.y = Math.trunc(this.options.height * scaleY) + 0.5;
    }
  };

  putImageData = (data: ImageData): void => {
    this.ctx.putImageData(data, 0, 0);
  };

  draw = (): void => {
    this.ctx.beginPath();
    this.ctx.moveTo(this.pointer.prevX, this.pointer.prevY);
    this.ctx.lineTo(this.pointer.x, this.pointer.y);
    this.ctx.stroke();
  };

  updateFrame = (): void => {
    if (this.pointer.isPressed && this.withinBounds(this.pointer)) {
      this.draw();
      this.drawBuffer.push({
        x: Math.trunc(this.pointer.prevX),
        y: Math.trunc(this.pointer.prevY),
      });
      this.drawBuffer.push({
        x: Math.trunc(this.pointer.x),
        y: Math.trunc(this.pointer.y),
      });
    }
    requestAnimationFrame(this.updateFrame);
  };

  withinBounds = (pointer: CanvasPointer): boolean => {
    return (
      pointer.x >= 0 &&
      pointer.x <= this.options.width &&
      pointer.y >= 0 &&
      pointer.y <= this.options.height
    );
  };

  pushToServer = (): void => {
    if (!this.drawBuffer.length) return;

    const bounds = this.getBoundingBox();

    console.log(bounds);

    const array = this.ctx.getImageData(
      bounds.minX,
      bounds.minY,
      bounds.maxX - bounds.minX || 1,
      bounds.maxY - bounds.minY || 1,
    ).data;

    console.log("data:", array);

    const frame = this.canvasFrameConverter.toBytes({
      startX: bounds.minX,
      startY: bounds.minY,
      endX: bounds.maxX,
      endY: bounds.maxY,
      data: array,
    });

    this.ws.send(
      this.messageEncoder.encode({
        timestamp: Date.now(),
        channel: "canvas",
        action: "update",
        payload: frame,
      }),
    );
  };

  getBoundingBox = () => {
    const buffer = this.drawBuffer;
    let minX = buffer[0].x;
    let minY = buffer[0].y;
    let maxX = buffer[0].x;
    let maxY = buffer[0].y;

    while (buffer.length) {
      const coord = buffer.pop()!;
      if (coord.x < minX) {
        minX = coord.x;
      }

      if (coord.y < minY) {
        minY = coord.y;
      }

      if (coord.x > maxX) {
        maxX = coord.x;
      }

      if (coord.y > maxY) {
        maxY = coord.y;
      }
    }

    minX -= Math.trunc(this.options.lineWidth / 2);
    minY -= Math.trunc(this.options.lineWidth / 2);
    maxX += Math.trunc(this.options.lineWidth / 2);
    maxY += Math.trunc(this.options.lineWidth / 2);

    return {
      minX,
      minY,
      maxX,
      maxY,
    };
  };
}
