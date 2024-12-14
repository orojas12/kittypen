const WS_URL = "ws://localhost:8080";

type CanvasEvent = {
  type: string;
  data: any;
};

type ServerEvent = {
  type: string;
  data: any;
  session: string;
};

type Line = {
  startX: number;
  startY: number;
  endX: number;
  endY: number;
  rgba: { r: number; g: number; b: number; a: number };
};

type CanvasOptions = {
  width: number;
  height: number;
  lineWidth: number;
};

export class Canvas {
  private options: CanvasOptions;
  private ctx: CanvasRenderingContext2D;
  private ws: WebSocket;

  private eventQueue: CanvasEvent[];

  private mouse = {
    lastX: 0,
    lastY: 0,
    x: 0,
    y: 0,
    lmb: false,
    rmb: false,
  };

  constructor(
    canvas?: HTMLCanvasElement,
    ws?: WebSocket,
    options?: Partial<CanvasOptions>,
  ) {
    this.eventQueue = [];

    this.options = {
      width: 4,
      height: 4,
      lineWidth: 1,
      ...options,
    };

    this.ctx = this.initCanvas(canvas || document.createElement("canvas"));

    this.ws = ws || new WebSocket(WS_URL);

    this.ws.onopen = (_) => {
      console.log("Established connection to server");
    };

    this.ws.addEventListener("message", this.handleMessage);

    document.addEventListener("pointerdown", this.handlePointerEvent);
    document.addEventListener("pointermove", this.handlePointerEvent);
    document.addEventListener("pointerup", this.handlePointerEvent);

    requestAnimationFrame(this.draw);

    this.processEventQueue();
  }

  initCanvas = (canvas: HTMLCanvasElement): CanvasRenderingContext2D => {
    canvas.width = this.options.width;
    canvas.height = this.options.height;

    const ctx = canvas.getContext("2d") as CanvasRenderingContext2D;

    ctx.lineWidth = this.options.lineWidth;
    ctx.lineJoin = "round";
    ctx.lineCap = "round";

    return ctx;
  };

  base64ToUint8ClampedArray = (base64: string): Uint8ClampedArray => {
    return Uint8ClampedArray.from(atob(base64), (char: string) =>
      char.charCodeAt(0),
    );
  };

  processEventQueue = () => {
    this.dedupeEvents();
    this.sendEvents();
    setTimeout(this.processEventQueue, 250);
  };

  dedupeEvents = () => {
    if (!this.eventQueue.length) return;

    const arr = [] as CanvasEvent[];
    let current = this.eventQueue[0];
    let event;

    arr.push(current);

    for (let i = 1; i < this.eventQueue.length; i++) {
      event = this.eventQueue[i];
      if (
        event.type === current.type &&
        event.data.startX === current.data.startX &&
        event.data.startY === current.data.startY &&
        event.data.endX === current.data.endX &&
        event.data.endY === current.data.endY &&
        event.data.rgba.r === current.data.rgba.r &&
        event.data.rgba.g === current.data.rgba.g &&
        event.data.rgba.b === current.data.rgba.b &&
        event.data.rgba.a === current.data.rgba.a
      ) {
        continue;
      } else {
        arr.push(event);
        current = event;
      }
    }

    this.eventQueue = arr;
  };

  sendEvents = () => {
    if (!this.eventQueue.length) return;

    let json = "";

    const sentEvents = [];

    for (const event of this.eventQueue) {
      json = JSON.stringify(event);
      this.ws.send(json);
      sentEvents.push(json);
    }

    console.log("Sent events: ", sentEvents);

    this.eventQueue = [];
  };

  draw = () => {
    if (this.mouse.lmb) {
      this.eventQueue.push({
        type: "drawLine",
        data: {
          startX: Math.round(this.mouse.lastX),
          startY: Math.round(this.mouse.lastY),
          endX: Math.round(this.mouse.x),
          endY: Math.round(this.mouse.y),
          rgba: { r: 0, g: 0, b: 0, a: 255 },
        },
      });
    }
    requestAnimationFrame(this.draw);
  };

  handlePointerEvent = (event: PointerEvent): void => {
    const bounds = this.ctx.canvas.getBoundingClientRect();

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

    const x = event.pageX - bounds.left;
    const y = event.pageY - bounds.top;

    // get the percentage of the display width/height and use it
    // to scale the x and y coordinate
    this.mouse.x = (x / bounds.width) * this.ctx.canvas.width;
    this.mouse.y = (y / bounds.height) * this.ctx.canvas.height;
  };

  handleMessage = (event: MessageEvent): void => {
    const serverEvent = JSON.parse(event.data) as ServerEvent;
    if (serverEvent.type === "canvasUpdate") {
      const data = this.base64ToUint8ClampedArray(serverEvent.data);
      console.log(data);
      this.putCanvasData(data);
    }
  };

  drawLine = (line: Line) => {
    const event = {
      type: "drawLine",
      data: line,
    } as CanvasEvent;

    console.log(event);

    this.ws.send(JSON.stringify(event));
  };

  putCanvasData = (data: Uint8ClampedArray) => {
    this.ctx.putImageData(
      new ImageData(data, this.options.width, this.options.height),
      0,
      0,
    );
  };
}
