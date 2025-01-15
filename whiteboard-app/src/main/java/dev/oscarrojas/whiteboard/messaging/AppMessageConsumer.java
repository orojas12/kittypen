package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AppMessageConsumer {

    private final List<ConsumerMethod> consumerMethods;

    public AppMessageConsumer() {
        List<ConsumerMethod> methods = new ArrayList<>();

        for (Method method : this.getClass().getDeclaredMethods()) {
            Action action = method.getAnnotation(Action.class);

            if (action == null) {
                continue;
            }

            methods.add(new ConsumerMethod(action.value(), method));
        }

        this.consumerMethods = methods;
    }

    public Iterable<ConsumerMethod> getConsumerMethods() {
        return consumerMethods;
    }

    public static class ConsumerMethod {
        public String action;
        public Method method;

        ConsumerMethod(String action, Method method) {
            this.action = action;
            this.method = method;
        }
    }
}
