package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.exception.InvalidInputException;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.messaging.AppMessageConsumer;
import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
@Channel("canvas")
public class CanvasMessageConsumer extends AppMessageConsumer {

  private final AppSessionService sessionService;

  public CanvasMessageConsumer(AppSessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Action("update")
  private void update(AppMessage message, String connectionId) {
    Optional<AppSession> sessionOpt = sessionService.getSessionForConnection(connectionId);

    if (sessionOpt.isEmpty()) {
      return;
    }

    AppSession session = sessionOpt.get();

    try {
      session.getCanvas().putData(message.getPayload());
    } catch (InvalidInputException e) {
      // TODO: figure out how to send error message
      return;
    }

    sessionService.saveSession(session);

    AppMessage appMessage = new AppMessage("canvas", "update", session.getCanvas().getData());

    try {
      session.broadcastMessage(appMessage, Collections.singletonList(connectionId));
    } catch (IOException exc) {
      throw new RuntimeException(exc);
    }
  }
}
