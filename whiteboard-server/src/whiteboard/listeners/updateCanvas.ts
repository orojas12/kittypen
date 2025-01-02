import { EventListener } from "../event-emitter";

export const base64ToCanvasData = (base64: string): Uint8ClampedArray => {
  return new Uint8ClampedArray(Buffer.from(base64, "base64"));
};

export const canvasDataToBase64 = (data: Uint8ClampedArray) => {
  const binaryStr = data.reduce(
    (acc, value) => acc + String.fromCharCode(value),
    "",
  );
  return btoa(binaryStr);
};

const updateCanvas: EventListener = (event, session) => {
  const data = base64ToCanvasData(event.data as string);
  session.state.canvas.putData(data);
  const base64 = canvasDataToBase64(data);
  session.broadcast("updateCanvas", base64, { exclude: [event.client.id] });
};

export default updateCanvas;
