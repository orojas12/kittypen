import type WebSocket from "ws";
import { WebSocketServer } from "ws";
import { WhiteboardSession } from "./whiteboard-session";
import { WhiteboardClient } from "./whiteboard-client";

const TICK_RATE = 30;
const FRAME_DURATION_MS = 1000 / TICK_RATE;

export class WhiteboardServer {
  private isRunning: boolean;
  private sessions: Map<string, WhiteboardSession>;
  private server: WebSocketServer;

  constructor() {
    this.isRunning = false;
    this.sessions = new Map();
    this.server = new WebSocketServer({ port: 8080 });

    this.server.on("connection", this.addClientToSession);
  }

  addClientToSession = (ws: WebSocket): void => {
    const client = new WhiteboardClient(ws);

    if (this.sessions.size === 0) {
      this.createSession([client]);
    } else {
      let id = "";
      // get first session id in sessions map
      for (const sessionId of this.sessions.keys()) {
        if (sessionId) {
          id = sessionId;
          break;
        }
      }
      this.sessions.get(id)!.addClient(client);
    }
  };

  start = () => {
    this.isRunning = true;
    this.tick();
  };

  tick = () => {
    if (this.isRunning) {
      this.sessions.forEach((session) => {
        // console.log(`publishing state for session ${session.id}`);
        session.publishState();
      });
      setTimeout(this.tick, FRAME_DURATION_MS);
    }
  };

  stop = () => {
    this.isRunning = false;
    this.sessions.forEach((session) => {
      session.close();
    });
    this.server.close();
  };

  createSession = (clients: WhiteboardClient[]) => {
    const session = new WhiteboardSession();

    clients.forEach((client) => session.addClient(client));
    this.sessions.set(session.id, session);
  };
}
