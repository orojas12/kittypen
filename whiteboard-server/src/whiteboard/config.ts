import { EventEmitter } from "./event-emitter";
import updateCanvas from "./listeners/updateCanvas";

const eventEmitter = new EventEmitter();

eventEmitter.on("updateCanvas", updateCanvas);

export { eventEmitter };
