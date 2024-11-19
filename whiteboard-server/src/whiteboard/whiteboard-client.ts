import type { WebSocket, RawData } from "ws";
import { randomUUID } from "crypto";

export class WhiteboardClient {
  id: string;
  private ws: WebSocket;

  constructor(ws: WebSocket) {
    this.id = randomUUID();
    this.ws = ws;
  }

  setMessageHandler = (
    handler: (message: RawData, client: WhiteboardClient) => void,
  ) => {
    this.ws.on("message", (message) => handler(message, this));
  };

  pushState = (data: Readonly<Uint8ClampedArray>): void => {
    this.ws.send(data);
  };

  close = (): void => {
    console.log(`client ${this.id} closed`);
    this.ws.terminate();
  };
}
