import { test, expect, vi } from "vitest";
import WebSocket from "ws";

import { WhiteboardClient } from "../whiteboard-client";
import { Whiteboard } from "../whiteboard";

vi.mock("ws");

test("terminates dead connections", async () => {
  const options = {
    maxPingAttempts: 2,
    pingInterval: 50,
  };
  const wb = new Whiteboard(options);
  const ws = new WebSocket(null);
  const client = new WhiteboardClient(ws);

  client.close = vi.fn();

  wb.addClient(client);

  return new Promise<void>((resolve) => {
    setTimeout(() => {
      expect(client.pingAttempts).toEqual(options.maxPingAttempts);
      expect(client.close).toHaveBeenCalledOnce();
      resolve();
    }, 200);
  });
});
