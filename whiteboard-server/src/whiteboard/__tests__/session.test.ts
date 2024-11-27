import { test, expect, vi } from "vitest";
import WebSocket from "ws";
import { EventEmitter } from "events";

import { Client } from "../client";
import { Session } from "../session";
import { Whiteboard } from "../whiteboard";

vi.mock("ws");

test("terminates dead connections", async () => {
  const options = {
    maxPingAttempts: 2,
    pingInterval: 50,
  };
  const session = new Session(new Whiteboard(), new EventEmitter(), options);
  const ws = new WebSocket(null);
  const client = new Client(ws);

  client.close = vi.fn();

  session.addClient(client);

  return new Promise<void>((resolve, reject) => {
    setTimeout(() => {
      try {
        expect(client.pingAttempts).toEqual(options.maxPingAttempts);
        expect(client.close).toHaveBeenCalledOnce();
      } catch (error) {
        reject(error);
      }
      resolve();
    }, 200);
  });
});
