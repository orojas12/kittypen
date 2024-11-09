type WhiteboardOptions = {
  internalWidth?: number;
  internalHeight?: number;
  defaultLineWidth?: number;
};

export class Whiteboard {
  private ctx: CanvasRenderingContext2D;
  private defaultWidth = 100;
  private defaultHeight = 100;
  private lineWidth = 4;
  private mouse = {
    x: 0,
    lastX: 0,
    y: 0,
    lastY: 0,
    lmb: false,
    rmb: false,
  };

  constructor(ctx: CanvasRenderingContext2D, options?: WhiteboardOptions) {
    this.ctx = ctx;
    this.defaultWidth = options?.internalWidth || this.defaultWidth;
    this.defaultHeight = options?.internalHeight || this.defaultHeight;
    this.lineWidth = options?.defaultLineWidth || this.lineWidth;

    this.initCanvas(this.ctx.canvas);
    this.initContext(this.ctx);

    document.addEventListener("pointerdown", this.handleMouseEvent);
    document.addEventListener("pointermove", this.handleMouseEvent);
    document.addEventListener("pointerup", this.handleMouseEvent);

    requestAnimationFrame(this.draw);
    this.connect();
  }

  initContext = (ctx: CanvasRenderingContext2D) => {
    ctx.lineWidth = this.lineWidth;
    ctx.lineCap = "round";
  };

  initCanvas = (canvas: HTMLCanvasElement) => {
    canvas.width = this.defaultWidth;
    canvas.height = this.defaultHeight;
  };

  handleMouseEvent = (event: PointerEvent) => {
    const bounds = this.ctx.canvas.getBoundingClientRect();
    event.pointerType;

    if (event.type === "pointerdown") {
      if (event.pointerType === "mouse") {
        if (event.button === 0) {
          this.mouse.lmb = true;
        }
      } else {
        this.mouse.lmb = true;
      }
    } else if (event.type === "pointerup") {
      this.mouse.lmb = false;
      this.mouse.rmb = false;
    }

    this.mouse.lastX = this.mouse.x;
    this.mouse.lastY = this.mouse.y;

    this.mouse.x = event.pageX - bounds.left;
    this.mouse.y = event.pageY - bounds.top;
    this.mouse.x = (this.mouse.x / bounds.width) * this.ctx.canvas.width;
    this.mouse.y = (this.mouse.y / bounds.height) * this.ctx.canvas.height;
  };

  draw = () => {
    if (this.mouse.lmb) {
      this.ctx.beginPath();
      this.ctx.moveTo(this.mouse.lastX, this.mouse.lastY);
      this.ctx.lineTo(this.mouse.x, this.mouse.y);
      this.ctx.stroke();
    }

    requestAnimationFrame(this.draw);
  };

  reset = () => {
    this.ctx.reset();
    this.initContext(this.ctx);
  };

  connect = () => {
    const ws = new WebSocket("ws://localhost:8080");
    ws.addEventListener("message", (event: MessageEvent) => {
      console.log(event.data);
    });
  };
}

// TODO: fix mobile pointer detection
// TODO: implement server
// TODO: add undo button
// TODO: add eraser
