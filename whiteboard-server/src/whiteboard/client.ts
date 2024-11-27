import { randomUUID } from "crypto";
import type WebSocket from "ws";

export type ClientMessage = {
  event: string;
  data: any;
};

export type ClientMessageListener = (
  event: string,
  data: any,
  client: Client,
) => void;

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
      // this.pingAttempts = 0;
    });
  }

  onMessage = (listener: ClientMessageListener): void => {
    this.ws.on("message", (data) => {
      const clientMessage = JSON.parse(data.toString()) as ClientMessage;
      listener(clientMessage.event, clientMessage.data, this);
    });
  };

  ping = (): void => {
    this.ws.ping();
    this.pingAttempts++;
    console.log("ping attempts: " + this.pingAttempts);
  };

  close = (): void => {
    this.ws.close();
  };
}
