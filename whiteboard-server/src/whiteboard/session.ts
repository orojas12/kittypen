import { randomUUID } from "crypto";
import type { Whiteboard } from "./whiteboard";
import { Client } from "./client";
import { ClientMessage } from "./types";
import EventEmitter from "events";

type WhiteboardSessionOptions = {
  maxPingAttempts: number;
  pingInterval: number;
  maxClients: number;
};

const defaultSessionOptions = {
  maxPingAttempts: 2,
  pingInterval: 5000,
  maxClients: 10,
};

export type SessionEventListener = (
  event: {
    id: string;
    data: any;
  },
  client: Client,
  session: Session,
) => void;

export class Session {
  id: string;
  private whiteboard: Whiteboard;
  private clients: Map<string, Client>;
  private eventEmitter: EventEmitter;
  private readonly options: WhiteboardSessionOptions;

  constructor(
    whiteboard: Whiteboard,
    eventEmitter: EventEmitter,
    options?: Partial<WhiteboardSessionOptions>,
  ) {
    this.id = randomUUID();
    this.clients = new Map();
    this.whiteboard = whiteboard;
    this.eventEmitter = eventEmitter;
    this.options = {
      ...defaultSessionOptions,
      ...options,
    };
  }

  addClient = (client: Client) => {
    client.onMessage(this.onClientMessage);
    this.clients.set(client.id, client);
    this.pingClient(client);
  };

  onClientMessage = (event: string, data: any, client: Client): void => {
    this.eventEmitter.emit(event, data, client, this);
  };

  isFull = () => {
    return this.clients.size >= this.options.maxClients;
  };

  pingClient = (client: Client): void => {
    if (client.pingAttempts === this.options.maxPingAttempts) {
      console.log(`Connection to client ${client.id} was lost.`);
      client.close();
      this.clients.delete(client.id);
      return;
    }

    client.ping();

    setTimeout(() => this.pingClient(client), this.options.pingInterval);
  };

  close = () => {
    this.clients.forEach((client) => client.close());
    this.clients.clear();
  };
}
