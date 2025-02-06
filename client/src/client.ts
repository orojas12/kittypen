import { Canvas } from "./canvas/canvas";
import AppEventBinaryConverter from "./messaging/AppEventBinaryConverter";

import type EventEmitter from "./messaging/EventEmitter";
import type { EventListener } from "./types";
import type { CanvasFrame } from "./canvas/types";
import { AppEvent } from "./messaging/types";

export class WhiteboardClient {
  private ws: WebSocket;
  private messageConverter: AppEventBinaryConverter;
  eventEmitter: EventEmitter;
  canvas: Canvas;

  constructor(eventEmitter: EventEmitter, canvas: Canvas) {
    this.ws = new WebSocket("ws://localhost:8080");
    this.ws.binaryType = "arraybuffer";
    this.eventEmitter = eventEmitter;
    this.canvas = canvas;
    this.messageConverter = new AppEventBinaryConverter();

    this.ws.addEventListener("message", this.handleMessage);
    this.canvas.onFrameUpdate(this.onCanvasUpdate);
  }

  private handleMessage = (message: MessageEvent): void => {
    console.log(message);
    const event = this.messageConverter.fromBytes(message.data);
    this.eventEmitter.emit(event.name, event, this);
  };

  private onCanvasUpdate = (frame: CanvasFrame): void => {
    const event: AppEvent<CanvasFrame> = {
      timestamp: Date.now(),
      name: "canvas.update",
      payload: frame,
    };
    this.eventEmitter.emit(event.name, event, this);
  };

  send = (event: AppEvent<ArrayBuffer>): void => {
    this.ws.send(this.messageConverter.toBytes(event));
  };

  addEventListener = (
    event: string,
    listener: EventListener<unknown>,
  ): void => {
    this.eventEmitter.on(event, listener);
  };
}
