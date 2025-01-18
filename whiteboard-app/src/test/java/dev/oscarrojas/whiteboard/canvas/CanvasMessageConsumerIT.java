package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
class CanvasMessageConsumerIT {

    @Autowired
    AppSessionService service;

    @Autowired
    CanvasMessageConsumer consumer;

    @Test
    void update_updatesCanvasData() {
        WebSocketSession ws = mock(StandardWebSocketSession.class);
        when(ws.getId()).thenReturn("ws1");
        AppSession session = service.getSession(ws);
        AppMessage appMessage = new AppMessage("canvas", "update", new byte[]{1, 1, 1, 1});

        consumer.update(appMessage, ws);
        session = service.getSession(session.getId()).get();

        byte[] data = session.getCanvas().getData();

        for (int i = 0; i < data.length; i++) {
            assertEquals(appMessage.getPayload()[i], data[i]);
        }
    }

}