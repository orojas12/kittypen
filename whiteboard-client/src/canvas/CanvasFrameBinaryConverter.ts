import type { CanvasFrame } from "./types";

export default class CanvasFrameBinaryConverter {
  toBytes = (frame: CanvasFrame): ArrayBuffer => {
    const view = new DataView(new ArrayBuffer(4 * 4 + frame.data.length));
    let pos = 0;

    view.setUint32(pos, frame.startX);
    pos += 4;

    view.setUint32(pos, frame.startY);
    pos += 4;

    view.setUint32(pos, frame.endX);
    pos += 4;

    view.setUint32(pos, frame.endY);
    pos += 4;

    for (let i = 0; i < frame.data.length; i++) {
      view.setUint8(pos++, frame.data[i]);
    }

    return view.buffer;
  };

  fromBytes = (bytes: ArrayBuffer): CanvasFrame => {
    const frame = {} as CanvasFrame;
    const view = new DataView(bytes);
    let pos = 0;

    frame.startX = view.getUint32(pos);
    pos += 4;

    frame.startY = view.getUint32(pos);
    pos += 4;

    frame.endX = view.getUint32(pos);
    pos += 4;

    frame.endY = view.getUint32(pos);
    pos += 4;

    const width = frame.endX - frame.startX;
    const height = frame.endY - frame.startY;

    frame.data = new Uint8ClampedArray(width * height * 4);

    for (let i = 0; i < frame.data.length; i++) {
      frame.data[i] = view.getUint8(pos++);
    }

    return frame;
  };
}
