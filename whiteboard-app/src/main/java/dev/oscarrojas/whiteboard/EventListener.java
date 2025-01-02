package dev.oscarrojas.whiteboard;

import java.util.List;

public interface EventListener {

  List<String> getEvents();

  void handleEvent(String event, AppSession session, byte[] payload);

}
