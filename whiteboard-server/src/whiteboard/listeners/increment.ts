import type { ClientEventListener } from "../session";

let onIncrement: ClientEventListener;

onIncrement = function (event, session) {
  session.state.counter++;
  session.broadcast();
};

export default onIncrement;
