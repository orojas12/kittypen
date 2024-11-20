import type { WhiteboardEventEmitter, WhiteboardState } from "../types";

export class CounterEventEmitter implements WhiteboardEventEmitter {
  keys = ["counter"];

  emit = (oldState: WhiteboardState, newState: WhiteboardState) => {
    if (newState.counter - oldState.counter === 1) {
      return ["increment"];
    } else return [];
  };
}
