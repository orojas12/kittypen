import "./style.css";
import { Canvas } from "./canvas/canvas";
import { WhiteboardClient } from "./client";
import { eventEmitter } from "./config";
import { AppEvent } from "./messaging/types";
import { AppSession } from "./types";

const canvasElement1 = document.getElementById("canvas1") as HTMLCanvasElement;

if (canvasElement1 === null) {
  throw Error("Canvas is null");
}

const ctx1 = canvasElement1.getContext("2d", {
  willReadFrequently: true,
}) as CanvasRenderingContext2D;

const canvas1 = new Canvas(ctx1, {
  width: 1000,
  height: 1000,
  lineWidth: 4,
});

const client = new WhiteboardClient(eventEmitter, canvas1, {
  username: "Oscar",
  eventListeners: [
    {
      name: "session.putDetails",
      callback: (event: AppEvent<unknown>, client: WhiteboardClient) => {
        const session = event.payload as AppSession;
        console.log(
          `Session: \nid = ${session.id}\nusers = [${session.users.join(", ")}]`,
        );
      },
    },
  ],
});

const resetBtn = document.getElementById("reset") as HTMLButtonElement;
resetBtn.addEventListener("click", () => {
  canvas1.clear();
});
