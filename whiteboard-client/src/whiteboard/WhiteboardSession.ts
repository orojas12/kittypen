import { Whiteboard } from "./Whiteboard";

type SessionEvent = {
  type: string;
  payload: any;
};

export class WhiteboardSession {
  private whiteboard: Whiteboard;
  private ws: WebSocket;

  constructor(whiteboard: Whiteboard) {
    this.whiteboard = whiteboard;
    this.ws = new WebSocket("ws://localhost:8080");

    this.ws.addEventListener("message", (event: MessageEvent<string>) => {
      const sessionEvent = JSON.parse(event.data) as SessionEvent;

      switch (sessionEvent.type) {
        case "setWhiteboard":
          const binaryString = atob(sessionEvent.payload);
          const bytes = new Uint8ClampedArray(binaryString.length);
          for (let i = 0; i < bytes.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }
          this.whiteboard.setData(sessionEvent.payload);
          break;

        default:
          break;
      }
    });
  }
}
