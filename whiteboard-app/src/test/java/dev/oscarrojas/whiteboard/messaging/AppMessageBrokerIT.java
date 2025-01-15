package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class AppMessageBrokerIT {

    @Autowired
    AppMessageBroker broker;

    @Autowired
    Executor executor;

    @Test
    void validatesSubscribingConsumers() {
        InvalidParamsMockConsumer consumer = new InvalidParamsMockConsumer();

        assertThrows(
            RuntimeException.class, () -> {
                broker.subscribe("mockChannel", consumer);
            }
        );

        MockConsumer mockConsumer = new MockConsumer();

        broker.subscribe("mockChannel", mockConsumer);
        broker.unsubscribe(mockConsumer);
    }

    @Test
    void publish_PublishesMessageToConsumersInChannel() throws ExecutionException, InterruptedException {
        MockConsumer consumer = spy(new MockConsumer());

        broker.subscribe("mockChannel", consumer);

        Future<Void> future = broker.publish(
            "mockChannel",
            new AppMessage("mockChannel", "mockAction", new byte[0]),
            ""
        );

        future.get();

        verify(consumer, times(1)).action();
    }

    @Channel("mockChannel")
    static class MockConsumer extends AppMessageConsumer {

        @Action("mockAction")
        public void action() {
        }
    }

    @Channel("mockChannel")
    static class InvalidParamsMockConsumer extends AppMessageConsumer {

        @Action("mockAction")
        public void action(Object obj) {
        }
    }
}
