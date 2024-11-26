import type WebSocket from "ws";
import { WebSocketServer } from "ws";
import { Whiteboard } from "./whiteboard";
import { WhiteboardClient } from "./whiteboard-client";

const TICK_RATE = 30;
const FRAME_DURATION_MS = 1000 / TICK_RATE;

type WhiteboardServerOptions = {
  port: number;
};

export class WhiteboardServer {
  private isRunning: boolean;
  private sessions: Map<string, Whiteboard>;
  private server: WebSocketServer;
  private defaultSession: Whiteboard;

  constructor(options?: WhiteboardServerOptions) {
    this.isRunning = false;
    this.sessions = new Map();
    this.server = new WebSocketServer({ port: options?.port || 8080 });
    this.defaultSession = new Whiteboard();

    this.server.on("connection", this.handleNewConnection);
  }

  handleNewConnection = (ws: WebSocket) => {
    const client = new WhiteboardClient(ws);
    this.defaultSession.addClient(client);
  };

  close = () => {
    this.defaultSession.close();
    this.server.close();
  };

  /** Adds an event listener that gets called once the server
   * is ready to accept connections. */
  onReady = (cb: () => void): void => {
    this.server.on("listening", cb);
  };
}
