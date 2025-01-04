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

export const canvasDataToBase64 = (bytes: Uint8ClampedArray): string => {
  const str = bytes.reduce(
    (acc, current) => acc + String.fromCharCode(current),
    "",
  );

  return btoa(str);
};

drawLine = (event, session) => {
  const data = event.data as Line;
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
