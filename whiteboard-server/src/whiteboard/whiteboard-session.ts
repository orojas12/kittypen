import { Canvas } from "./canvas";
import { randomUUID } from "crypto";
import { WhiteboardClient } from "./whiteboard-client";
import type { SessionEvent, SessionEventHandler } from "./types";
import type { RawData } from "ws";

export class WhiteboardSession {
  id: string;
  counter: number;
  private canvas: Canvas;
  private clients: WhiteboardClient[];
  private eventHandlers: Map<string, SessionEventHandler[]>;

  constructor() {
    this.id = randomUUID();
    this.canvas = new Canvas();
    this.clients = [] as WhiteboardClient[];
    this.eventHandlers = new Map();
    this.counter = 0;
  }

  publishState = (): void => {
    const data = this.canvas.getData();
    this.clients.forEach((client) => {
      client.pushState(data);
    });
  };

  close = (): void => {
    console.log("closing session...");
    this.clients.forEach((client) => {
      client.close();
    });
  };

  addClient = (client: WhiteboardClient): void => {
    client.setMessageHandler(this.handleClientMessage);
    this.clients.push(client);
    console.log(`Added client ${client.id} to session ${this.id}`);
  };

  handleClientMessage = (message: RawData, client: WhiteboardClient) => {
    const sessionEvent = JSON.parse(message.toString()) as SessionEvent;
    sessionEvent.clientId = client.id;
    this.handleEvent(sessionEvent);
  };

  addEventHandler = (handler: SessionEventHandler): void => {
    const handlerList = this.eventHandlers.get(handler.event);

    if (!handlerList) {
      this.eventHandlers.set(handler.event, [handler]);
    } else {
      handlerList.push(handler);
    }
  };

  handleEvent = (event: SessionEvent): void => {
    const handlerList = this.eventHandlers.get(event.id);

    if (!handlerList) {
      console.log(`No event handler found for event '${event.id}'`);
      return;
    }

    for (const handler of handlerList) {
      handler.handleEvent(event, this);
    }
  };
}
