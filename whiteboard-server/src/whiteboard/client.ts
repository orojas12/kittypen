import { randomUUID } from "crypto";
import type WebSocket from "ws";
import type { ClientMessage } from "./types";

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

  onMessage = (listener: (message: ClientMessage) => void): void => {
    this.ws.on("message", (data) => {
      const clientMessage = JSON.parse(data.toString()) as ClientMessage;
      clientMessage.clientId = this.id;
      listener(clientMessage);
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
