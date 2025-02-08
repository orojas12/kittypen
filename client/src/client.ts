import { Canvas } from "./canvas/canvas";
import AppEventBinaryConverter from "./messaging/AppEventBinaryConverter";

import type EventEmitter from "./messaging/EventEmitter";
import type { AppSession, EventListener } from "./types";
import type { CanvasFrame } from "./canvas/types";
import { AppEvent } from "./messaging/types";

export type WhiteboardClientConfig = {
  username: string;
  eventListeners: { name: string; callback: EventListener<any> }[];
};

export class WhiteboardClient {
  private config: WhiteboardClientConfig;
  private ws: WebSocket;
  private messageConverter: AppEventBinaryConverter;
  private csrfToken: string;
  eventEmitter: EventEmitter;
  canvas: Canvas;

  constructor(
    eventEmitter: EventEmitter,
    canvas: Canvas,
    config?: WhiteboardClientConfig,
  ) {
    this.config = config || { username: "default", eventListeners: [] };
    this.eventEmitter = eventEmitter;
    this.canvas = canvas;
    this.messageConverter = new AppEventBinaryConverter();

    this.csrfToken = "";
    const url = new URL("ws://localhost:8080/whiteboard");

    this.initSession().then(() => {
      this.ws = new WebSocket(url);
      this.ws.addEventListener("open", this.onConnect);
      this.ws.addEventListener("message", this.handleMessage);
      this.ws.binaryType = "arraybuffer";
      this.canvas.onFrameUpdate(this.onCanvasUpdate);

      this.config.eventListeners.forEach((listener) => {
        this.eventEmitter.on(listener.name, listener.callback);
      });
    });
  }

  private initSession = async (): Promise<void> => {
    let res = await fetch("http://localhost:8080/auth/csrf");

    this.csrfToken =
      document.cookie
        .split("; ")
        .find((row) => row.startsWith("XSRF-TOKEN="))
        ?.split("=")[1] || "";

    console.log("csrf token: " + this.csrfToken);
    res = await fetch("http://localhost:8080/auth/session", {
      method: "POST",
      body: JSON.stringify({
        username: this.config.username,
      }),
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": this.csrfToken,
      },
      credentials: "include",
    });
  };

  private onConnect = (): void => {
    this.ws.send(
      JSON.stringify({
        timestamp: Date.now(),
        name: "session.getDetails",
        payload: null,
      } as AppEvent<null>),
    );
    this.ws.send(
      this.messageConverter.toBytes({
        timestamp: Date.now(),
        name: "canvas.getCanvas",
        payload: null,
      }),
    );
  };

  private handleMessage = (message: MessageEvent): void => {
    console.log(message);
    let event;
    if (typeof message.data === "string") {
      event = JSON.parse(message.data);
    } else {
      event = this.messageConverter.fromBytes(message.data);
    }
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
