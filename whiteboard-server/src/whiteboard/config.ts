import { EventEmitter } from "./event-emitter";
import onIncrement from "./listeners/increment";

const eventEmitter = new EventEmitter();

eventEmitter.on("increment", onIncrement);

export { eventEmitter };
