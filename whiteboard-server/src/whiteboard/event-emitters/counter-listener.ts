import type { EventListener } from "./event-emitter";

export default class CounterListener implements EventListener {
  handleEvent = (event: string, counter: number) => {
    console.log("counter: " + counter);
  };
}
