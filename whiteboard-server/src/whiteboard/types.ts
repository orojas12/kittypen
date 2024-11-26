import type { Whiteboard } from "./whiteboard";
import type { WhiteboardSession } from "./whiteboard-session";

export type WhiteboardState = {
  counter: number;
};

export enum ClientAction {
  INCREMENT = "increment",
}

export type ClientMessage = {
  action: string;
  clientId: string;
  data: unknown;
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
  handleEvent: (event: SessionEvent, session: WhiteboardSession) => void;
};
