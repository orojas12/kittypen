import { EventListener } from "../event-emitter";

let onIncrement: EventListener;

onIncrement = function (event, session) {
  session.state.counter++;
  session.broadcast("counter", session.state.counter);
};

export default onIncrement;
