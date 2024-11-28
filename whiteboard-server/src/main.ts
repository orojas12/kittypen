import { Server } from "./whiteboard/server";

import { eventEmitter } from "./whiteboard/config";

const server = new Server({ eventEmitter });
