import type { Session } from "./session";

export type WhiteboardState = {
  counter: number;
};

export type WhiteboardEventListener = {
  event: string;
  handleEvent: (event: string, state: WhiteboardState) => void;
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
  handleEvent: (event: SessionEvent, session: Session) => void;
};
