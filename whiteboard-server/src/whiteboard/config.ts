import { EventEmitter } from "./event-emitter";
import onIncrement from "./listeners/increment";
import drawLine from "./listeners/draw-line";

const eventEmitter = new EventEmitter();

eventEmitter.on("increment", onIncrement);
eventEmitter.on("drawLine", drawLine);

export { eventEmitter };
