import type { WhiteboardSession } from "./whiteboard-session";

export type SessionEvent = {
  id: string;
  clientId: string;
  data: unknown;
};

export type SessionEventHandler = {
  event: string;
  handleEvent: (event: SessionEvent, session: WhiteboardSession) => void;
};
