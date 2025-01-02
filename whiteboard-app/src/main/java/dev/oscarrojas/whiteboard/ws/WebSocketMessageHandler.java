package dev.oscarrojas.whiteboard.ws;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.EventEmitter;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessage;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;

@Component
public class WebSocketMessageHandler extends BinaryWebSocketHandler {

  private EventEmitter eventEmitter;
  private AppSessionService sessionService;
  private AppMessageBinaryEncoder encoder;

  public WebSocketMessageHandler(EventEmitter eventEmitter,
      AppSessionService sessionService, AppMessageBinaryEncoder encoder) {
    this.eventEmitter = eventEmitter;
    this.sessionService = sessionService;
    this.encoder = encoder;
  }

  @Override
  protected void handleBinaryMessage(WebSocketSession connection, BinaryMessage message) throws Exception {
    AppMessage appMessage = encoder.decode(message.getPayload());
    Optional<AppSession> appSession = sessionService.getSessionForConnection(connection.getId());

    if (appSession.isEmpty()) {
      throw new RuntimeException("Null app session");
    }

    eventEmitter.emit(appMessage.getEvent(), appSession.get(), appMessage.getPayload());
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession connection) {
    sessionService.registerConnection(connection);
  }

}
