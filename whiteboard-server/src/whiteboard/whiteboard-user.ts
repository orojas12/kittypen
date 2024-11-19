import { randomUUID } from "crypto";
import type WebSocket from "ws";
import type { UserEvent } from "./types";

export class WhiteboardUser {
  id: string;
  private ws: WebSocket;

  constructor(ws: WebSocket) {
    this.id = randomUUID();
    this.ws = ws;
  }

  addEventListener = (listener: (event: UserEvent) => void): void => {
    this.ws.on("message", (data) => {
      const event = JSON.parse(data.toString()) as UserEvent;
      event.userId = this.id;
      listener(event);
    });
  };
}
