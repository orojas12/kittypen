import { randomUUID } from "crypto";
import { WhiteboardClient } from "./whiteboard-client";

import { ClientAction, ClientMessage, WhiteboardState } from "./types";
import { EventEmitter } from "events";

function increment(state: WhiteboardState) {
  state.counter++;
}

type WhiteboardEventHandler = (state: WhiteboardState) => void;

const WhiteboardEventHandlers = {
  increment: [increment],
} as Record<string, WhiteboardEventHandler[] | undefined>;

type WhiteboardOptions = {
  eventEmitter?: EventEmitter;
  maxPingAttempts?: number;
  clientPingInterval?: number;
  maxClients?: number;
};

export class Whiteboard {
  private readonly maxClients: number;
  private readonly maxPingAttempts: number;
  private readonly clientPingInterval: number;
  private clients: Map<string, WhiteboardClient>;
  private eventEmitter: EventEmitter;

  id: string;

  state: WhiteboardState;

  constructor(options?: WhiteboardOptions) {
    this.maxClients = options?.maxClients || 10;
    this.maxPingAttempts = options?.maxPingAttempts || 2;
    this.clientPingInterval = options?.clientPingInterval || 5000;
    this.eventEmitter = options?.eventEmitter || new EventEmitter();
    this.clients = new Map();
    this.id = randomUUID();
    this.state = {
      counter: 0,
    };
  }

  setEventEmitter = (eventEmitter: EventEmitter): void => {
    this.eventEmitter = eventEmitter;
  };

  close = (): void => {
    this.clients.forEach((client, key) => {
      client.close();
      this.clients.delete(key);
    });
  };

  addClient = (client: WhiteboardClient) => {
    client.onMessage(this.handleClientMessage);
    this.clients.set(client.id, client);
    this.pingClient(client);
  };

  handleClientMessage = (message: ClientMessage): void => {
    const handlers = WhiteboardEventHandlers[message.event];

    if (handlers) {
      for (let i = 0; i < handlers.length; i++) {
        handlers[i](this.state);
      }
    }
  };

  isFull = () => {
    return this.clients.size >= this.maxClients;
  };

  pingClient = (client: WhiteboardClient): void => {
    if (client.pingAttempts === this.maxPingAttempts) {
      console.log(`Connection to client ${client.id} was lost.`);
      client.close();
      this.clients.delete(client.id);
      return;
    }

    client.ping();

    setTimeout(() => this.pingClient(client), this.clientPingInterval);
  };
}
