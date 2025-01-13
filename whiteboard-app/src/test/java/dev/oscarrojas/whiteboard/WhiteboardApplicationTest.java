package dev.oscarrojas.whiteboard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.ws.WebSocketMessageHandler;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WhiteboardApplicationTest {

  @Autowired WebSocketMessageHandler messageHandler;

  @Autowired AppSessionService sessionService;

  @Mock WebSocketSession ws;

  @Test
  void registersNewConnection() {
    when(ws.getId()).thenReturn("ws1");

    AppMessageBinaryEncoder encoder = new AppMessageBinaryEncoder();
    BinaryMessage msg =
        new BinaryMessage(encoder.encode(new AppMessage("channel1", "action1", new byte[] {0})));

    messageHandler.afterConnectionEstablished(ws);

    AppSession session = sessionService.getSessionForConnection(ws.getId()).orElseThrow();
    assertTrue(session.hasConnection(ws.getId()));
  }
}
