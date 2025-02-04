import { WhiteboardClient } from "../client";
import type { EventListener } from "../types";
import { AppEvent } from "./types";

export default class EventEmitter {
  eventListeners: Map<string, EventListener<unknown>[]> = new Map();

  on = (event: string, listener: EventListener<any>): void => {
    const listeners = this.eventListeners.get(event);
    if (listeners) {
      listeners.push(listener);
    } else {
      this.eventListeners.set(event, [listener]);
    }
  };

  emit = (
    eventName: string,
    event: AppEvent<unknown>,
    client: WhiteboardClient,
  ): void => {
    const listeners = this.eventListeners.get(eventName);
    if (listeners) {
      for (const handle of listeners) {
        handle(event, client);
      }
    }
  };
}
