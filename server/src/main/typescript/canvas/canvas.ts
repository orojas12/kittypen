type CanvasFrame = {
  startX: number;
  startY: number;
  endX: number;
  endY: number;
  data: Uint8ClampedArray;
};

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

type Coordinate = {
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
  private drawBuffer: Coordinate[];

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
    const data = this.ctx.getImageData(
      0,
      0,
      this.config.width,
      this.config.height,
    ).data;
    this.handleFrameUpdate({
      startX: 0,
      startY: 0,
      endX: this.config.width,
      endY: this.config.height,
      data,
    });
  };

  putFrame = (frame: CanvasFrame): void => {
    this.ctx.putImageData(
      new ImageData(
        frame.data as ImageDataArray,
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

  private handleFrameUpdate = (frame: CanvasFrame) => {
    this.drawBuffer.length = 0;
  };

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
    if (event.type === "pointerdown") {
      this.pointer.x = this.getPointerX(event);
      this.pointer.y = this.getPointerY(event);
      this.pointer.prevX = this.pointer.x;
      this.pointer.prevY = this.pointer.y;
      this.pointer.isPressed = true;
    } else if (event.type === "pointerup") {
      this.pointer.isPressed = false;
    } else if (event.type === "pointermove") {
      this.pointer.x = this.getPointerX(event);
      this.pointer.y = this.getPointerY(event);
    }
  };

  private getPointerX = (event: PointerEvent): number => {
    const domRect = this.ctx.canvas.getBoundingClientRect();
    const x = event.pageX - domRect.left;
    const scaleX = x / domRect.width;
    return Math.trunc(this.config.width * scaleX) + 0.5;
  };

  private getPointerY = (event: PointerEvent): number => {
    const domRect = this.ctx.canvas.getBoundingClientRect();
    const y = event.pageY - domRect.top;
    const scaleY = y / domRect.height;
    return Math.trunc(this.config.height * scaleY) + 0.5;
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

    // account for half the line width so the stroke fits
    // completely inside the box
    const offset = Math.trunc(this.config.lineWidth / 2);

    return {
      minX: minX - offset,
      minY: minY - offset,
      maxX: maxX + offset,
      maxY: maxY + offset,
    };
  };
}
