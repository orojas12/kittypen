package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AppMessageBroker {

    private final Map<String, LinkedList<AppMessageConsumer>> channels = new HashMap<>();

    public void subscribe(String channel, AppMessageConsumer consumer) {

        validateConsumerMethods(consumer);

        LinkedList<AppMessageConsumer> consumers = channels.get(channel);

        if (consumers == null) {
            channels.put(
                channel,
                new LinkedList<>(Collections.singletonList(consumer))
            );
        } else {
            consumers.add(consumer);
        }

    }

    /**
     * Removes a consumer from a channel
     *
     * @param consumer consumer to be removed if present
     * @return true if this channel contained specified consumer
     */
    public boolean unsubscribe(AppMessageConsumer consumer) {
        Channel channel = consumer.getClass().getAnnotation(Channel.class);

        if (channel == null) {
            return false;
        }

        LinkedList<AppMessageConsumer> consumers = channels.get(
            channel.value());

        return consumers.remove(consumer);
    }

    @Async
    public Future<Void> publish(
        String channel,
        AppMessage message,
        WebSocketSession ws
    ) {
        LinkedList<AppMessageConsumer> consumers = channels.get(channel);

        if (consumers == null) {
            return CompletableFuture.completedFuture(null);
        }

        for (AppMessageConsumer consumer : consumers) {

            for (AppMessageConsumer.ConsumerMethod consumerMethod : consumer.getConsumerMethods()) {

                if (consumerMethod.action.equals(message.getAction())) {
                    try {
                        Parameter[] params = consumerMethod.method.getParameters();
                        Object[] args = new Object[params.length];

                        for (int i = 0; i < params.length; i++) {
                            if (params[i].getType() == AppMessage.class) {
                                args[i] = message;
                            } else if (params[i].getType() == WebSocketSession.class) {
                                args[i] = ws;
                            }
                        }

                        consumerMethod.method.invoke(consumer, args);
                    } catch (IllegalAccessException e) {
                        // TODO: proper error logging
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e.getTargetException());
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    private void validateConsumerMethods(AppMessageConsumer consumer) {

        for (AppMessageConsumer.ConsumerMethod consumerMethod : consumer.getConsumerMethods()) {

            Parameter[] params = consumerMethod.method.getParameters();
            int messageCount = 0;
            int wsCount = 0;

            for (Parameter param : params) {
                if (param.getType() == AppMessage.class &&
                    messageCount == 0
                ) {
                    messageCount++;
                    continue;
                } else if (param.getType() == WebSocketSession.class &&
                    wsCount == 0
                ) {
                    wsCount++;
                    continue;
                } else {
                    throw new RuntimeException(String.format(
                        "Invalid " +
                            "parameter(s) for method '%s' of consumer '%s'",
                        consumerMethod.method.getName(),
                        consumer.getClass().getSimpleName()
                    ));
                }
            }
        }
    }
}
