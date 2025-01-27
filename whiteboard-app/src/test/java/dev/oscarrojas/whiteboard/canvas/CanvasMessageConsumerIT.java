package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import dev.oscarrojas.whiteboard.session.InMemoryAppSessionDao;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import dev.oscarrojas.whiteboard.ws.protocol.BinaryDecodingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
class CanvasMessageConsumerIT {

    @Autowired
    AppSessionService service;

    @Autowired
    InMemoryAppSessionDao sessionDao;

    @Autowired
    AppMessageBinaryEncoder encoder;

    @Autowired
    CanvasMessageConsumer consumer;

    @AfterEach
    void teardown() {
        sessionDao.deleteAll();
    }

    @Test
    void update_updatesCanvasData() {
        WebSocketSession ws = mock(StandardWebSocketSession.class);
        when(ws.getId()).thenReturn("ws1");
        AppSession session = new AppSession(
            "session1",
            new Canvas(1, 1),
            Map.of("ws1", ws),
            encoder
        );
        sessionDao.save(session);
        AppMessage message = new AppMessage("canvas", "update", new byte[]{1, 1, 1, 1});

        consumer.update(message, ws);
        session = service.getSession(session.getId()).get();

        byte[] data = session.getCanvas().getData();

        for (int i = 0; i < data.length; i++) {
            assertEquals(message.getPayload()[i], data[i]);
        }
    }

    @Test
    void update_broadcastsUpdateToOtherConnections() throws IOException {
        WebSocketSession ws1 = mock(StandardWebSocketSession.class);
        WebSocketSession ws2 = mock(StandardWebSocketSession.class);
        when(ws1.getId()).thenReturn("ws1");
        when(ws1.isOpen()).thenReturn(true);
        when(ws2.getId()).thenReturn("ws2");
        when(ws2.isOpen()).thenReturn(true);
        AppSession session = new AppSession(
            "session1",
            new Canvas(1, 1),
            Map.of("ws1", ws1, "ws2", ws2),
            encoder
        );
        sessionDao.save(session);

        AppMessage message = new AppMessage("canvas", "update", new byte[]{1, 1, 1, 1});
        consumer.update(message, ws1);

        verify(ws1, times(0)).sendMessage(any());
        verify(ws2, times(1)).sendMessage(argThat((arg) -> {
            try {
                AppMessage msg = encoder.decode((ByteBuffer) arg.getPayload());
                assertEquals(message.getChannel(), msg.getChannel());
                assertEquals(message.getAction(), msg.getAction());
                byte[] payload1 = message.getPayload();
                byte[] payload2 = msg.getPayload();
                for (int i = 0; i < payload1.length; i++) {
                    assertEquals(payload1[i], payload2[i]);
                }
                return true;
            } catch (BinaryDecodingException e) {
                throw new RuntimeException(e);
            }
        }));

    }

}