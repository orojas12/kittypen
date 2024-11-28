import EventEmitter from "events";
import onIncrement from "./listeners/increment";

const eventEmitter = new EventEmitter();

eventEmitter.on("increment", onIncrement);

export { eventEmitter };
