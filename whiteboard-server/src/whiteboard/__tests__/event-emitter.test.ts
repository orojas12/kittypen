import { test, expect, vi } from "vitest";

import { EventEmitter } from "../event-emitters/event-emitter";

test("calls correct event listeners on emit", () => {
  const event1 = "event1";
  const event2 = "event2";
  const listener1 = {
    handleEvent: vi.fn(),
  };
  const listener2 = {
    handleEvent: vi.fn(),
  };
  const emitter = new EventEmitter();
  emitter.on(event1, listener1);
  emitter.on(event2, listener2);

  emitter.emit(event1, null);

  expect(listener1.handleEvent).toBeCalledTimes(1);
  expect(listener2.handleEvent).toBeCalledTimes(0);

  emitter.emit(event2, null);

  expect(listener1.handleEvent).toBeCalledTimes(1);
  expect(listener2.handleEvent).toBeCalledTimes(1);
});
