import { EventEmitter as NodeEventEmitter } from "events";
import type { ClientEvent } from "./client";
import type { Session, SessionEvent } from "./session";

export type EventListener = (
  event: ClientEvent,
  session: Session,
) => SessionEvent | void;

/**
 * Type-safe wrapper class for node's {@link https://nodejs.org/api/events.html#class-eventemitter EventEmitter} class.
 */
export class EventEmitter {
  private eventEmitter = new NodeEventEmitter();

  on = (event: string, listener: EventListener): void => {
    this.eventEmitter.on(event, listener);
  };

  emit = (event: ClientEvent, session: Session): void => {
    this.eventEmitter.emit(event.type, event, session);
  };
}
