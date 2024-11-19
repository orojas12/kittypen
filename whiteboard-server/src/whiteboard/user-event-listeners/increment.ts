import type { UserEvent, UserEventListener, WhiteboardState } from "../types";

export class IncrementUserEventListener implements UserEventListener {
  eventId = "increment";

  handleEvent = (event: UserEvent, state: WhiteboardState) => {
    return {
      counter: state.counter + 1,
    };
  };
}
