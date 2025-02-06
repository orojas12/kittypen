import type { CanvasFrame } from "./types";

export type CanvasConfig = {
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

type Coord = {
  x: number;
  y: number;
};

const DEFAULT_CANVAS_CONFIG = {
  width: 100,
  height: 100,
  lineWidth: 4,
};

export class Canvas {
  private config: CanvasConfig;
  private ctx: CanvasRenderingContext2D;
  private pointer: CanvasPointer;
  private drawBuffer: Coord[];

  constructor(ctx: CanvasRenderingContext2D, config?: Partial<CanvasConfig>) {
    this.config = {
      ...DEFAULT_CANVAS_CONFIG,
      ...config,
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

    this.setUpEventListeners();

    requestAnimationFrame(this.tick);

    setInterval(this.flushAndUpdate, 100);
  }

  clear = (): void => {
    this.ctx.clearRect(0, 0, this.config.width, this.config.height);
  };

  putFrame = (frame: CanvasFrame): void => {
    this.ctx.putImageData(
      new ImageData(
        frame.data,
        frame.endX - frame.startX,
        frame.endY - frame.startY,
      ),
      frame.startX,
      frame.startY,
    );
  };

  onFrameUpdate = (handler: (frame: CanvasFrame) => void): void => {
    this.handleFrameUpdate = handler;
  };

  private handleFrameUpdate = (frame: CanvasFrame) => {};

  private setUpContext = (
    ctx: CanvasRenderingContext2D,
  ): CanvasRenderingContext2D => {
    ctx.imageSmoothingEnabled = false;
    ctx.canvas.width = this.config.width;
    ctx.canvas.height = this.config.height;
    ctx.lineWidth = this.config.lineWidth;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    return ctx;
  };

  private setUpEventListeners = (): void => {
    document.addEventListener("pointerdown", this.handlePointerEvent);
    document.addEventListener("pointerup", this.handlePointerEvent);
    document.addEventListener("pointermove", this.handlePointerEvent);
  };

  private handlePointerEvent = (event: PointerEvent): void => {
    const domRect = this.ctx.canvas.getBoundingClientRect();

    if (event.type === "pointerdown") {
      this.pointer.isPressed = true;
      this.pointer.prevX = this.pointer.x;
      this.pointer.prevY = this.pointer.y;
    } else if (event.type === "pointerup") {
      this.pointer.isPressed = false;
    } else {
      this.pointer.x = event.pageX - domRect.left;
      this.pointer.y = event.pageY - domRect.top;

      const scaleX = this.pointer.x / domRect.width;
      const scaleY = this.pointer.y / domRect.height;

      this.pointer.x = Math.trunc(this.config.width * scaleX) + 0.5;
      this.pointer.y = Math.trunc(this.config.height * scaleY) + 0.5;
    }
  };

  private draw = (): void => {
    this.ctx.beginPath();
    this.ctx.moveTo(this.pointer.prevX, this.pointer.prevY);
    this.ctx.lineTo(this.pointer.x, this.pointer.y);
    this.ctx.stroke();
  };

  private tick = (): void => {
    if (this.pointer.isPressed && this.isInsideCanvas(this.pointer)) {
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
    this.pointer.prevX = this.pointer.x;
    this.pointer.prevY = this.pointer.y;
    requestAnimationFrame(this.tick);
  };

  private isInsideCanvas = (pointer: CanvasPointer): boolean => {
    return (
      pointer.x >= 0 &&
      pointer.x <= this.config.width &&
      pointer.y >= 0 &&
      pointer.y <= this.config.height
    );
  };

  private flushAndUpdate = (): void => {
    if (!this.drawBuffer.length) return;

    const bounds = this.getBoundingBox();
    const data = this.ctx.getImageData(
      bounds.minX,
      bounds.minY,
      bounds.maxX - bounds.minX || 1,
      bounds.maxY - bounds.minY || 1,
    ).data;

    this.handleFrameUpdate({
      startX: bounds.minX,
      startY: bounds.minY,
      endX: bounds.maxX,
      endY: bounds.maxY,
      data,
    });
  };

  private getBoundingBox = () => {
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

    minX -= Math.trunc(this.config.lineWidth / 2);
    minY -= Math.trunc(this.config.lineWidth / 2);
    maxX += Math.trunc(this.config.lineWidth / 2);
    maxY += Math.trunc(this.config.lineWidth / 2);

    return {
      minX,
      minY,
      maxX,
      maxY,
    };
  };
}
