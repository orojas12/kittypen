import AppMessageBinaryEncoder from "../messaging/AppMessageBinaryEncoder";
import { AppMessage } from "../messaging/types";

export type CanvasOptions = {
  width: number;
  height: number;
  lineWidth: number;
};

export type CanvasPointer = {
  x: number;
  y: number;
  prevX: number;
  prevY: number;
  isPressed: boolean;
};

const DEFAULT_CANVAS_OPTIONS = {
  width: 100,
  height: 100,
  lineWidth: 4,
};

export class Canvas {
  private options: CanvasOptions;
  private ctx: CanvasRenderingContext2D;
  private ws: WebSocket;
  private synced: boolean;
  private syncInterval: NodeJS.Timeout;
  private pointer: CanvasPointer;
  private encoder: AppMessageBinaryEncoder;

  constructor(ctx: CanvasRenderingContext2D, options?: Partial<CanvasOptions>) {
    this.encoder = new AppMessageBinaryEncoder();
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

    this.ctx = this.setUpContext(ctx);

    this.ws = this.establishServerConnection();

    this.synced = true;

    this.setUpEventListeners();

    requestAnimationFrame(this.updateFrame);

    this.syncInterval = setInterval(this.syncWithServer, 100);
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
      const message = this.encoder.decode(event.data) as AppMessage;
      console.log(
        `received message: {\ntimestamp: ${message.timestamp} \nchannel: ${message.channel}, \naction: ${message.action}, \npayload: ${message.payload}`,
      );

      const imgData = new ImageData(
        new Uint8ClampedArray(message.payload),
        this.options.width,
      );

      this.ctx.putImageData(imgData, 0, 0);
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

  draw = (): void => {
    this.ctx.beginPath();
    this.ctx.moveTo(this.pointer.prevX, this.pointer.prevY);
    this.ctx.lineTo(this.pointer.x, this.pointer.y);
    this.ctx.stroke();
  };

  updateFrame = (): void => {
    if (this.pointer.isPressed) {
      this.draw();
      this.synced = false;
    }
    requestAnimationFrame(this.updateFrame);
  };

  syncWithServer = (): void => {
    if (this.synced) return;

    console.log("Syncing data...");

    const array = this.ctx.getImageData(
      0,
      0,
      this.options.width,
      this.options.height,
    ).data;

    this.ws.send(
      this.encoder.encode({
        timestamp: Date.now(),
        channel: "canvas",
        action: "update",
        payload: array,
      }),
    );

    this.synced = true;
  };
}
