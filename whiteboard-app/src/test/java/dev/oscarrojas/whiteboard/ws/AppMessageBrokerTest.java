package dev.oscarrojas.whiteboard.ws;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class AppMessageBrokerTest {

  @Test
  void subscribeAndPublish() {
    AppMessageBroker broker = new AppMessageBroker();

    MessageConsumer consumer = mock(MessageConsumer.class);
    when(consumer.getChannels()).thenReturn(List.of("channel1"));

    broker.subscribe(consumer);
    AppMessage msg = new AppMessage("channel1", "action1", new byte[] {0});
    broker.publish("channel1", msg, "");

    verify(consumer, times(1)).receiveMessage(msg, "");

    broker.publish("channel0", msg, "");

    verify(consumer, times(1)).receiveMessage(msg, "");
  }

  class MessageConsumer implements AppMessageConsumer {

    @Override
    public List<String> getChannels() {
      return List.of();
    }

    @Override
    public void receiveMessage(AppMessage message, String connectionId) {}
  }
}
