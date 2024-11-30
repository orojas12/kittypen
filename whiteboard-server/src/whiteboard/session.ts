import { randomUUID } from "crypto";
import { EventEmitter } from "./event-emitter";

import { Canvas } from "./canvas";
import { Client, ClientEvent } from "./client";

export type SessionOptions = {
  maxPingAttempts: number;
  pingInterval: number;
  maxClients: number;
};

export type SessionState = {
  canvas: Canvas;
  counter: number;
};

export type SessionEvent = {
  type: string;
  data: any;
  session: string;
};

const DEFAULT_SESSION_OPTIONS = {
  maxPingAttempts: 2,
  pingInterval: 5000,
  maxClients: 10,
};

export class Session {
  id: string;
  state: SessionState;
  private clients: Map<string, Client>;
  private eventEmitter: EventEmitter;
  private readonly options: SessionOptions;

  constructor(
    eventEmitter: EventEmitter,
    options?: Partial<SessionOptions>,
    state?: SessionState,
  ) {
    this.id = randomUUID();
    this.clients = new Map();
    this.state = state || {
      counter: 0,
      canvas: new Canvas(),
    };
    this.eventEmitter = eventEmitter;
    this.options = {
      ...DEFAULT_SESSION_OPTIONS,
      ...options,
    };
  }

  /**
   * Broadcasts event to all clients.
   */
  broadcast = (event: string, data: any) => {
    this.clients.forEach((client) => {
      client.send({
        type: event,
        data: data,
        session: this.id,
      } as SessionEvent);
    });
  };

  /**
   * Adds a new websocket client to this session.
   */
  addClient = (client: Client) => {
    client.onEvent(this.onClientEvent);
    this.clients.set(client.id, client);
    this.pingClient(client);
  };

  /**
   * Handles incoming client events by passing each event to its
   * subscribed listener.
   */
  onClientEvent = (event: ClientEvent): void => {
    this.eventEmitter.emit(event, this);
  };

  /**
   * Returns true if this session has reached its max number of clients allowed.
   */
  isFull = () => {
    return this.clients.size >= this.options.maxClients;
  };

  /**
   * Recursively pings a websocket client and closes it if the connection is lost.
   */
  pingClient = (client: Client): void => {
    if (client.pingAttempts === this.options.maxPingAttempts) {
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
