package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BinaryAppEventEmitterIT {

    @Autowired
    AppEventEmitter emitter;

    @Test
    void validatesAddedEventListeners() {
        InvalidParamsMockListener listener = new InvalidParamsMockListener();

        assertThrows(
            RuntimeException.class, () -> {
                emitter.addEventListener("mockEvent", listener);
            }
        );

        MockListener mockListener = new MockListener();

        emitter.addEventListener("mockEvent", mockListener);
        emitter.removeEventListener(mockListener);
    }

    @Test
    void emit_EmitsEventsToListenersOfEvent() throws ExecutionException, InterruptedException {
        MockListener listener = spy(new MockListener());

        emitter.addEventListener("mockEvent", listener);

        Future<Void> future = emitter.emit(
            "mockEvent",
            new BinaryAppEvent("mockEvent", new byte[0]),
            new StandardWebSocketSession(null, null, null, null)
        );

        future.get();

        verify(listener, times(1)).handleEvent();
    }

    static class MockListener extends AppEventListener {

        @Event("mockEvent")
        public void handleEvent() {
        }
    }

    static class InvalidParamsMockListener extends AppEventListener {

        @Event("mockEvent")
        public void handleEvent(Object obj) {
        }
    }
}
