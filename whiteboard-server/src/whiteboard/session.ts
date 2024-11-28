import { randomUUID } from "crypto";
import EventEmitter from "events";

import { Canvas } from "./canvas";
import { Client, ClientEvent } from "./client";

export type SessionOptions = {
  maxPingAttempts: number;
  pingInterval: number;
  maxClients: number;
};

export type ClientEventListener = (
  event: ClientEvent,
  session: Session,
) => void;

export type SessionState = {
  canvas: Canvas;
  counter: number;
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
   * Broadcast this session's state to all clients.
   */
  broadcast = () => {
    this.clients.forEach((client) => {
      client.send(this.state);
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
    this.eventEmitter.emit(event.name, event, this);
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
