package dev.oscarrojas.whiteboard.ws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AppMessageBroker {

  private Map<String, LinkedList<AppMessageConsumer>> channels = new HashMap<>();

  public void subscribe(AppMessageConsumer consumer) {
    for (String channel : consumer.getChannels()) {
      LinkedList<AppMessageConsumer> consumers = channels.get(channel);

      if (consumers == null) {
        channels.put(channel, new LinkedList<>(Arrays.asList(consumer)));
      } else {
        consumers.add(consumer);
      }
    }
  }

  public void publish(String channel, AppMessage message, String connectionId) {
    LinkedList<AppMessageConsumer> consumers = channels.get(channel);

    if (consumers == null) {
      return;
    }

    for (AppMessageConsumer consumer : consumers) {
      consumer.receiveMessage(message, connectionId);
    }
  }
}
