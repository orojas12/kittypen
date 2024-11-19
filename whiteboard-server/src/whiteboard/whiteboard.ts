import { randomUUID } from "crypto";
import { WhiteboardUser } from "./whiteboard-user";

import type WebSocket from "ws";
import type {
  UserEvent,
  UserEventListener,
  WhiteboardEvent,
  WhiteboardEventListener,
  WhiteboardState,
} from "./types";

export class Whiteboard {
  private users: Map<string, WhiteboardUser>;
  private userEventListeners: Map<string, UserEventListener[]>;
  private whiteboardEventListeners: Map<string, WhiteboardEventListener[]>;

  id: string;

  state: WhiteboardState;

  constructor() {
    this.users = new Map();
    this.userEventListeners = new Map();
    this.whiteboardEventListeners = new Map();
    this.id = randomUUID();
    this.state = {
      counter: 0,
    };
  }

  handleNewConnection = (ws: WebSocket): void => {
    const user = new WhiteboardUser(ws);
    user.addEventListener(this.handleUserEvent);
  };

  handleUserEvent = (event: UserEvent): void => {
    const listeners = this.userEventListeners.get(event.id);

    if (!listeners) {
      return;
    }

    for (const listener of listeners) {
      const newState = listener.handleEvent(event, this.state);

      this.getMutatedStateKeys(this.state, newState).forEach((key) => {
        // emit whiteboard events based on event emitters
      });
    }
  };

  getMutatedStateKeys = (oldState: any, newState: any): string[] => {
    const mutatedKeys = [];
    for (const key in newState) {
      if (newState[key] !== oldState[key]) {
        mutatedKeys.push(key);
      }
    }
    return mutatedKeys;
  };

  handleWhiteboardEvent = (event: WhiteboardEvent): void => {
    const listeners = this.whiteboardEventListeners.get(event.id);

    if (!listeners) {
      return;
    }

    for (const listener of listeners) {
      listener.handleEvent(event, this);
    }
  };
}
