import { randomUUID } from "crypto";
import { WhiteboardUser } from "./whiteboard-user";

import type WebSocket from "ws";
import type {
  UserEvent,
  UserEventListener,
  WhiteboardEvent,
  WhiteboardEventEmitter,
  WhiteboardEventListener,
  WhiteboardState,
} from "./types";

export class Whiteboard {
  private users: Map<string, WhiteboardUser>;
  private userEventListeners: Map<string, UserEventListener[]>;
  private whiteboardEventListeners: Map<string, WhiteboardEventListener[]>;
  private whiteboardEventEmitters: Map<string, WhiteboardEventEmitter[]>;

  id: string;

  state: WhiteboardState;

  constructor() {
    this.users = new Map();
    this.userEventListeners = new Map();
    this.whiteboardEventListeners = new Map();
    this.whiteboardEventEmitters = new Map();
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
      const whiteboardEvents = new Set<string>();
      const mutatedKeys = this.getMutatedStateKeys(this.state, newState);

      for (const key of mutatedKeys) {
        const emitters = this.whiteboardEventEmitters.get(key);
        if (emitters) {
          for (const emitter of emitters) {
            const events = emitter.emit(this.state, newState);
            for (const event of events) {
              whiteboardEvents.add(event);
            }
          }
        }
      }
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
