package dev.oscarrojas.whiteboard;

import dev.oscarrojas.whiteboard.messaging.AppMessageBroker;
import dev.oscarrojas.whiteboard.ws.WebSocketMessageHandler;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
class WhiteboardApplicationIT {

    @Autowired
    WebSocketMessageHandler messageHandler;

    @Autowired
    AppSessionService sessionService;

    @Autowired
    AppMessageBroker messageBroker;

    @Autowired
    AppMessageBinaryEncoder encoder;

    @Test
    void registersNewConnection() {
        WebSocketSession ws = mock(StandardWebSocketSession.class);
        when(ws.getId()).thenReturn("ws1");

        messageHandler.afterConnectionEstablished(ws);

        AppSession session = sessionService.getSession(ws);

        assertTrue(session.hasConnection(ws.getId()));
        assertEquals(1, session.getConnectionCount());
    }

}
