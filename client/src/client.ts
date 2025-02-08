import { Canvas } from "./canvas/canvas";
import AppEventBinaryConverter from "./messaging/AppEventBinaryConverter";

import type EventEmitter from "./messaging/EventEmitter";
import type { AppSession, EventListener } from "./types";
import type { CanvasFrame } from "./canvas/types";
import { AppEvent } from "./messaging/types";

export type WhiteboardClientConfig = {
  username: string;
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
    this.config = config || { username: "default" };

    this.csrfToken = "";
    const url = new URL("ws://localhost:8080/whiteboard");
    url.searchParams.append("username", this.config.username);

    this.initSession();

    this.ws = new WebSocket(url);
    this.ws.binaryType = "arraybuffer";
    this.eventEmitter = eventEmitter;
    this.canvas = canvas;
    this.messageConverter = new AppEventBinaryConverter();

    this.ws.addEventListener("open", this.onConnect);
    this.ws.addEventListener("message", this.handleMessage);
    this.canvas.onFrameUpdate(this.onCanvasUpdate);
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
      this.messageConverter.toBytes({
        timestamp: Date.now(),
        name: "canvas.getCanvas",
        payload: null,
      }),
    );
  };

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

  getSessionDetails = async (): Promise<AppSession> => {};

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
