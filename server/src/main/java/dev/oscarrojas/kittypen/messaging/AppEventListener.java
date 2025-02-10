package dev.oscarrojas.kittypen.messaging;

import dev.oscarrojas.kittypen.messaging.annotation.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AppEventListener {

    private final List<ListenerMethod> listenerMethods;

    public AppEventListener() {
        List<ListenerMethod> methods = new ArrayList<>();

        for (Method method : this.getClass().getDeclaredMethods()) {
            Event event = method.getAnnotation(Event.class);

            if (event == null) {
                continue;
            }

            methods.add(new ListenerMethod(event.value(), method));
        }

        this.listenerMethods = methods;
    }

    public Iterable<ListenerMethod> getListenerMethods() {
        return listenerMethods;
    }

    public static class ListenerMethod {
        public String event;
        public Method method;

        ListenerMethod(String event, Method method) {
            this.event = event;
            this.method = method;
        }
    }
}
