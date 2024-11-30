import { randomUUID } from "crypto";
import type WebSocket from "ws";
import type { SessionEvent } from "./session";

export type ClientEvent = {
  type: string;
  data: any;
  client: Client;
};

export type ClientEventListener = (event: ClientEvent) => void;

export class Client {
  id: string;
  pingAttempts: number;
  isAlive: boolean;
  private ws: WebSocket;

  constructor(ws: WebSocket) {
    this.id = randomUUID();
    this.pingAttempts = 0;
    this.isAlive = true;
    this.ws = ws;

    this.ws.on("pong", () => {
      this.pingAttempts = 0;
    });
  }

  send = (data: SessionEvent) => {
    this.ws.send(JSON.stringify(data));
  };

  onEvent = (listener: ClientEventListener): void => {
    this.ws.on("message", (data) => {
      const clientEvent = JSON.parse(data.toString()) as ClientEvent;
      clientEvent.client = this;
      listener(clientEvent);
    });
  };

  ping = (): void => {
    this.ws.ping();
    this.pingAttempts++;
  };

  close = (): void => {
    this.ws.close();
  };
}
