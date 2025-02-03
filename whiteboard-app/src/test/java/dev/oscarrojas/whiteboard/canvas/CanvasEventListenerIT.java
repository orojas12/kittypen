package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.messaging.AppEvent;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import dev.oscarrojas.whiteboard.session.InMemoryAppSessionDao;
import dev.oscarrojas.whiteboard.ws.protocol.AppEventBinaryConverter;
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
class CanvasEventListenerIT {

    @Autowired
    AppSessionService service;

    @Autowired
    InMemoryAppSessionDao sessionDao;

    @Autowired
    AppEventBinaryConverter eventConverter;

    @Autowired
    CanvasEventListener listener;

    @AfterEach
    void teardown() {
        sessionDao.deleteAll();
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
            new Canvas(10, 10),
            Map.of("ws1", ws1, "ws2", ws2),
            eventConverter
        );
        sessionDao.save(session);

        CanvasFrameBinaryConverter frameConverter = new CanvasFrameBinaryConverter();
        CanvasFrame frame = new CanvasFrame(0, 0, 1, 1, new byte[]{1, 1, 1, 1});
        AppEvent event = new AppEvent("canvas.update", frameConverter.toBytes(frame));
        listener.update(event, ws1);

        verify(ws1, times(0)).sendMessage(any());
        verify(ws2, times(1)).sendMessage(argThat((arg) -> {
            try {
                AppEvent receivedEvent = eventConverter.fromBytes((ByteBuffer) arg.getPayload());
                assertEquals(event.getName(), receivedEvent.getName());
                byte[] frame1 = event.getPayload();
                byte[] frame2 = receivedEvent.getPayload();
                for (int i = 0; i < frame1.length; i++) {
                    assertEquals(frame1[i], frame2[i]);
                }
                return true;
            } catch (BinaryDecodingException e) {
                throw new RuntimeException(e);
            }
        }));

    }

    @Test
    void update_updatesCanvasData() {
        WebSocketSession ws = mock(StandardWebSocketSession.class);
        when(ws.getId()).thenReturn("ws1");
        AppSession session = new AppSession(
            "session1",
            new Canvas(1, 1),
            Map.of("ws1", ws),
            eventConverter
        );
        sessionDao.save(session);
        CanvasFrameBinaryConverter frameConverter = new CanvasFrameBinaryConverter();
        CanvasFrame frame = new CanvasFrame(0, 0, 1, 1, new byte[]{1, 1, 1, 1});
        AppEvent event = new AppEvent("canvas.update", frameConverter.toBytes(frame));

        listener.update(event, ws);
        session = service.getSession(session.getId()).get();

        byte[] data = session.getCanvas().getData();

        for (int i = 0; i < data.length; i++) {
            assertEquals(event.getPayload()[i], data[i]);
        }
    }


}