export type EventListener = {
  handleEvent: (event: string, data: any) => void;
};

export class EventEmitter {
  private eventListeners = new Map<string, EventListener[]>();

  on = (event: string, listener: EventListener) => {
    const listeners = this.eventListeners.get(event);

    if (listeners) {
      listeners.push(listener);
    } else {
      this.eventListeners.set(event, [listener]);
    }
  };

  emit = (event: string, data: unknown) => {
    const listeners = this.eventListeners.get(event);

    if (listeners) {
      for (const listener of listeners) {
        listener.handleEvent(event, data);
      }
    }
  };
}
