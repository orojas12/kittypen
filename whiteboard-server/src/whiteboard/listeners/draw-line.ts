import { Rgba } from "../canvas";
import { EventListener } from "../event-emitter";

let drawLine: EventListener;

type Line = {
  startX: number;
  startY: number;
  endX: number;
  endY: number;
  rgba: Rgba;
};

const canvasDataToBase64 = (arr: Uint8ClampedArray): string => {
  return btoa(String.fromCharCode(...arr));
};

drawLine = (event, session) => {
  const data = event.data as Line;
  session.state.canvas.reset();
  session.state.canvas.drawLine(
    data.startX,
    data.startY,
    data.endX,
    data.endY,
    data.rgba,
  );
  session.broadcast(
    "canvasUpdate",
    canvasDataToBase64(session.state.canvas.getData()),
  );
};

export default drawLine;
