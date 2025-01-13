package dev.oscarrojas.whiteboard.messaging;

import java.util.List;

public interface AppMessageConsumer {

  List<String> getChannels();

  void receiveMessage(AppMessage message, String connectionId);
}
