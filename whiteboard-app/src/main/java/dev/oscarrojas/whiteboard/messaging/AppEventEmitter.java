package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Event;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AppEventEmitter {

    private final Map<String, ArrayList<AppEventListener>> eventListeners = new HashMap<>();

    public void addEventListener(String event, AppEventListener listener) {

        validateListenerMethods(listener);

        ArrayList<AppEventListener> listeners = eventListeners.get(event);

        if (listeners == null) {
            eventListeners.put(
                event,
                new ArrayList<>(Collections.singletonList(listener))
            );
        } else {
            listeners.add(listener);
        }

    }

    /**
     * Removes an event listener
     *
     * @param listener listener to be removed if present
     * @return true if the emitter contained specified listener
     */
    public boolean removeEventListener(AppEventListener listener) {
        Event event = listener.getClass().getAnnotation(Event.class);

        if (event == null) {
            return false;
        }

        ArrayList<AppEventListener> listeners = eventListeners.get(event.value());

        return listeners.remove(listener);
    }

    @Async
    public Future<Void> emit(
        String eventName,
        AppEvent<?> event,
        WebSocketSession ws
    ) {
        ArrayList<AppEventListener> listeners = eventListeners.get(eventName);

        if (listeners == null) {
            return CompletableFuture.completedFuture(null);
        }

        for (AppEventListener listener : listeners) {

            for (AppEventListener.ListenerMethod listenerMethod : listener.getListenerMethods()) {

                if (listenerMethod.event.equals(event.getName())) {
                    try {
                        Parameter[] params = listenerMethod.method.getParameters();
                        Object[] args = new Object[params.length];

                        for (int i = 0; i < params.length; i++) {
                            if (params[i].getType() == BinaryAppEvent.class ||
                                params[i].getType() == JsonAppEvent.class) {
                                args[i] = event;
                            } else if (params[i].getType() == WebSocketSession.class) {
                                args[i] = ws;
                            }
                        }

                        listenerMethod.method.invoke(listener, args);
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

    private void validateListenerMethods(AppEventListener listener) {

        for (AppEventListener.ListenerMethod listenerMethod : listener.getListenerMethods()) {

            Parameter[] params = listenerMethod.method.getParameters();
            int eventParams = 0;
            int wsParams = 0;

            for (Parameter param : params) {
                if (param.getType() == BinaryAppEvent.class && eventParams == 0) {
                    eventParams++;
                    continue;
                } else if (param.getType() == JsonAppEvent.class && eventParams == 0) {
                    eventParams++;
                    continue;
                } else if (param.getType() == WebSocketSession.class && wsParams == 0) {
                    wsParams++;
                    continue;
                } else {
                    throw new RuntimeException(String.format(
                        "Invalid " +
                            "parameter(s) for method '%s' of consumer '%s'",
                        listenerMethod.method.getName(),
                        listener.getClass().getSimpleName()
                    ));
                }
            }
        }
    }
}
