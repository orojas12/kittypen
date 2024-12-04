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

export class Canvas {
  private ctx: CanvasRenderingContext2D;
  private ws: WebSocket;

  constructor(ctx: CanvasRenderingContext2D, ws?: WebSocket) {
    this.ctx = ctx;

    this.ws = ws || new WebSocket(WS_URL);

    this.ws.onopen = (_) => {
      console.log("Established connection to server");
      setInterval(this.drawRandom, 2000);
    };

    this.ws.addEventListener("message", this.handleMessage);
  }

  base64ToCanvasData = (base64: string): Uint8ClampedArray => {
    return Uint8ClampedArray.from(atob(base64), (char: string) =>
      char.charCodeAt(0),
    );
  };

  handleMessage = (event: MessageEvent): void => {
    const serverEvent = JSON.parse(event.data) as ServerEvent;
    if (serverEvent.type === "canvasUpdate") {
      const data = this.base64ToCanvasData(serverEvent.data);
      console.log(data);
      this.putCanvasData(data);
    }
  };

  drawLine = (line: Line) => {
    const event = {
      type: "drawLine",
      data: line,
    } as CanvasEvent;

    this.ws.send(JSON.stringify(event));
  };

  putCanvasData = (data: Uint8ClampedArray) => {
    this.ctx.putImageData(new ImageData(data, 4, 4), 0, 0);
  };

  drawRandom = () => {
    const line = {
      startX: this.getRandom(0, 4),
      startY: this.getRandom(0, 4),
      endX: this.getRandom(0, 4),
      endY: this.getRandom(0, 4),
      rgba: {
        r: 255,
        g: 0,
        b: 0,
        a: 255,
      },
    };
    console.log(line);
    this.drawLine(line);
  };

  getRandom = (min: number, max: number): number => {
    return Math.floor(Math.random() * (max - min) + min);
  };
}
