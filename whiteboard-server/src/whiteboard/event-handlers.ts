import type { SessionEvent, SessionEventHandler } from "./types";

export const eventHandlers: SessionEventHandler[] = [
  {
    event: "increment",
    handleEvent: (event: SessionEvent, session) => {
      session.counter++;
      console.log(`counter: ${session.counter}`);
    },
  },
];
