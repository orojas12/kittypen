package dev.oscarrojas.whiteboard.messaging;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.scheduling.annotation.Async;

public class AppMessageBroker {

  private final Map<String, LinkedList<AppMessageConsumer>> channels = new HashMap<>();

  public void subscribe(String channel, AppMessageConsumer consumer) {
    LinkedList<AppMessageConsumer> consumers = channels.get(channel);

    if (consumers == null) {
      channels.put(channel, new LinkedList<>(Collections.singletonList(consumer)));
    } else {
      consumers.add(consumer);
    }
  }

  @Async
  public void publish(String channel, AppMessage message, String connectionId) {
    LinkedList<AppMessageConsumer> consumers = channels.get(channel);

    if (consumers == null) {
      return;
    }

    for (AppMessageConsumer consumer : consumers) {
      for (AppMessageConsumer.ActionMethod actionMethod : consumer.getActionMethods()) {
        if (actionMethod.action.equals(message.getAction())) {
          try {
            actionMethod.method.invoke(consumer, message, connectionId);
          } catch (IllegalAccessException e) {
            // TODO: proper error logging
            throw new RuntimeException(e);
          } catch (InvocationTargetException e) {
            System.out.println(e.getCause().getMessage());
          }
        }
      }
    }
  }
}
