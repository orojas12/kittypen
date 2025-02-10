package dev.oscarrojas.kittypen.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.AppEventBinaryConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
class AppSessionServiceIT {

    @Autowired
    AppSessionService service;

    @Autowired
    AppSessionDao sessionDao;

    @Autowired
    AppEventBinaryConverter converter;

    @Autowired
    ObjectMapper mapper;

    @Test
    void getSession_addsNewConnectionToExistingSessionIfNotFull() {
        AppSession session1 = new AppSession(
            UUID.randomUUID().toString(), new Canvas(1, 1),
            converter, mapper
        );
        sessionDao.save(session1);

        WebSocketSession ws = mock(StandardWebSocketSession.class);
        when(ws.getId()).thenReturn("ws1");

        service.getSession(ws);
        Optional<AppSession> sessionOpt = sessionDao.get(session1.getId());
        assertEquals(1, sessionOpt.get().getConnectionCount());

    }

}