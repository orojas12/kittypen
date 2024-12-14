import { EventEmitter } from "./event-emitter";
import type WebSocket from "ws";
import { WebSocketServer } from "ws";
import { Client } from "./client";
import { Session } from "./session";
import { Canvas } from "./canvas";

export type ServerOptions = {
  port: number;
  eventEmitter: EventEmitter;
};

const DEFAULT_SERVER_OPTIONS = {
  port: 8080,
  eventEmitter: new EventEmitter(),
};

export class Server {
  private server: WebSocketServer;
  private defaultSession: Session;
  private options: ServerOptions;

  constructor(options?: Partial<ServerOptions>) {
    this.options = {
      ...DEFAULT_SERVER_OPTIONS,
      ...options,
    };

    this.server = new WebSocketServer({
      port: this.options.port,
    });

    this.defaultSession = new Session(this.options.eventEmitter, {
      canvas: new Canvas(100, 100),
    });

    this.server.on("connection", this.onConnection);

    console.log("Server listening on port " + this.options.port);
  }

  onConnection = (ws: WebSocket) => {
    const client = new Client(ws);
    this.defaultSession.addClient(client);
  };

  close = () => {
    this.defaultSession.close();
    this.server.close();
  };
}
