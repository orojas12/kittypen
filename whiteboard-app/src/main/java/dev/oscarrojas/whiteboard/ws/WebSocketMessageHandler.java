package dev.oscarrojas.whiteboard.ws;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Component
public class WebSocketMessageHandler extends BinaryWebSocketHandler {

  private AppMessageBroker messageBroker;
  private AppSessionService sessionService;
  private AppMessageBinaryEncoder encoder;

  public WebSocketMessageHandler(
      AppMessageBroker messageBroker,
      AppSessionService sessionService,
      AppMessageBinaryEncoder encoder) {
    this.messageBroker = messageBroker;
    this.sessionService = sessionService;
    this.encoder = encoder;
  }

  @Override
  protected void handleBinaryMessage(WebSocketSession ws, BinaryMessage message) throws Exception {
    AppMessage appMessage = encoder.decode(message.getPayload());
    Optional<AppSession> appSession = sessionService.getSessionForConnection(ws.getId());

    if (appSession.isEmpty()) {
      throw new RuntimeException("Null app session");
    }

    messageBroker.publish(appMessage.getChannel(), appMessage, ws.getId());
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession ws) {
    sessionService.registerConnection(ws);
  }
}
