import type { Whiteboard } from "./whiteboard";
import type { WhiteboardSession } from "./whiteboard-session";

export type WhiteboardState = {
  counter: number;
};

export type UserEvent = {
  id: string;
  userId: string;
  data: unknown;
};

export type UserEventListener = {
  eventId: string;
  handleEvent: (event: UserEvent, state: WhiteboardState) => WhiteboardState;
};

export type WhiteboardEvent = {
  id: string;
  keys: string[];
  data: unknown;
};

export type WhiteboardEventListener = {
  eventId: string;
  handleEvent: (event: WhiteboardEvent, whiteboard: Whiteboard) => void;
};

export type WhiteboardEventEmitter = {
  keys: string[];
  emit: (oldState: WhiteboardState, newState: WhiteboardState) => string[];
};

export type SessionEvent = {
  id: string;
  clientId: string;
  data: unknown;
};

export type SessionEventHandler = {
  event: string;
  handleEvent: (event: SessionEvent, session: WhiteboardSession) => void;
};
