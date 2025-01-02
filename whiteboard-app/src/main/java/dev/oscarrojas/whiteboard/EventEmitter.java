package dev.oscarrojas.whiteboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EventEmitter {

  private Map<String, List<EventListener>> listeners;

  EventEmitter(List<EventListener> listeners) {
    this.listeners = new HashMap<>();

    for (EventListener listener : listeners) {
      addEventListener(listener);
    }

  }

  public void emit(String event, AppSession session, byte[] payload) {
    List<EventListener> eventListeners = listeners.get(event);

    if (eventListeners == null) {
      return;
    }

    for (EventListener listener : eventListeners) {
      listener.handleEvent(event, session, payload);
    }
  }

  public void addEventListener(EventListener listener) {

    for (String event : listener.getEvents()) {

      List<EventListener> eventListeners = listeners.get(event);

      if (eventListeners == null) {
        listeners.put(event, new ArrayList<>(Arrays.asList(listener)));
      } else {
        eventListeners.add(listener);
      }

    }

  }

}
