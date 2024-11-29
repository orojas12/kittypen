import { test, expect, vi } from "vitest";
import { EventEmitter } from "../event-emitter";

import { Client } from "../client";
import { Session } from "../session";

const mockWs = {
  on: vi.fn(),
  ping: vi.fn(),
};

test("terminates dead connections", async () => {
  const options = {
    maxPingAttempts: 2,
    pingInterval: 50,
  };
  const session = new Session(new EventEmitter(), options);
  const client = new Client(mockWs as any);

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

test("broadcasts events", async () => {
  const eventEmitter = new EventEmitter();

  eventEmitter.on("event1", (event, session) => {
    expect(event.type).toEqual("event1");
    expect(event.data).toEqual(1);
    session.broadcast("event2", 2);
  });

  const session = new Session(eventEmitter);
  const client = new Client(mockWs as any);

  client.id = "client1";
  client.send = vi.fn();

  session.addClient(client);

  session.onClientEvent({ type: "event1", data: 1, client });

  expect(client.send).toBeCalledTimes(1);
  expect(client.send).toBeCalledWith({
    type: "event2",
    data: 2,
    session: session.id,
  });
});
