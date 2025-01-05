package dev.oscarrojas.whiteboard.ws;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class AppMessageBrokerTest {

  @Test
  void subscribeAndPublish() {
    AppMessageBroker broker = new AppMessageBroker();

    MessageConsumer consumer1 = mock(MessageConsumer.class);
    MessageConsumer consumer2 = mock(MessageConsumer.class);
    MessageConsumer consumer3 = mock(MessageConsumer.class);
    when(consumer1.getChannels()).thenReturn(List.of("channel1"));
    when(consumer2.getChannels()).thenReturn(List.of("channel2"));
    when(consumer3.getChannels()).thenReturn(List.of("channel1"));

    broker.subscribe(consumer1);
    broker.subscribe(consumer2);
    broker.subscribe(consumer3);
    AppMessage msg1 = new AppMessage("channel1", "action1", new byte[] {0});
    AppMessage msg2 = new AppMessage("channel2", "action2", new byte[] {0});

    broker.publish("channel1", msg1, "");

    verify(consumer1, times(1)).receiveMessage(msg1, "");
    verify(consumer2, never()).receiveMessage(any(), any());
    verify(consumer3, times(1)).receiveMessage(msg1, "");

    broker.publish("channel2", msg2, "");

    verify(consumer1, times(1)).receiveMessage(msg1, "");
    verify(consumer2, times(1)).receiveMessage(msg2, "");
    verify(consumer3, times(1)).receiveMessage(msg1, "");
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
