const WS_URL = "ws://localhost:8080";

type CanvasEvent = {
  type: string;
  data: any;
};

type Line = {
  startX: number;
  startY: number;
  endX: number;
  endY: number;
};

export class Canvas {
  private ctx: CanvasRenderingContext2D;
  private ws: WebSocket;

  constructor(ctx: CanvasRenderingContext2D, ws?: WebSocket) {
    this.ctx = ctx;

    this.ws = ws || new WebSocket(WS_URL);

    this.ws.addEventListener("message", this.handleMessage);
  }

  handleMessage = (event: MessageEvent): void => {
    const data = JSON.parse(event.data);
    console.log(data);
  };

  drawLine = (line: Line) => {
    const event = {
      type: "drawLine",
      data: line,
    };

    this.ws.send(JSON.stringify(event));
  };

  putImageData = (data: ImageData) => {
    this.ctx.putImageData(data, 0, 0);
  };
}
