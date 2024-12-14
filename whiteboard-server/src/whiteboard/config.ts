import { EventEmitter } from "./event-emitter";
import drawLine from "./listeners/draw-line";

const eventEmitter = new EventEmitter();

eventEmitter.on("drawLine", drawLine);

export { eventEmitter };
