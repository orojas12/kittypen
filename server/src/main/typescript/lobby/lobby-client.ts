export class LobbyClient {
  ws: WebSocket | null;

  constructor(url: string) {
    try {
      this.ws = new WebSocket(url);
    } catch (e) {
      console.error(e);
      this.ws = null;
      return;
    }

    this.setUpConnection(this.ws);
  }

  setUpConnection(ws: WebSocket) {
    ws.addEventListener("open", (e) => {
      console.debug(`Established connection to ${ws.url}`);
    });

    ws.addEventListener("error", (e) => {
      console.error(`Failed to connect to ${ws.url}`);
    });

    ws.addEventListener("close", (e) => {
      console.debug(`Connection to ${ws.url} was closed`);
    });

    ws.addEventListener("message", (e) => {
      console.debug(`Received message from ${ws.url}: ${e.data}`);
    });
  }
}
