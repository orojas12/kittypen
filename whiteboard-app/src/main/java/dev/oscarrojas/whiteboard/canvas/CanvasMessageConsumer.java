package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.AppSessionService;
import dev.oscarrojas.whiteboard.exception.InvalidInputException;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.messaging.AppMessageConsumer;
import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

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
        Canvas canvas = session.getCanvas();

        // update message's timestamp is older than canvas' most recent update
        if (message.getTimestamp().compareTo(canvas.getLastUpdated()) < 0) {
            // abort update as this message is outdated and canvas currently
            // reflects the state from a more recent update message
            return;
        }

        try {
            canvas.putData(message.getPayload());
        } catch (InvalidInputException e) {
            // TODO: figure out how to send error message
            return;
        }

        sessionService.saveSession(session);

        AppMessage appMessage = new AppMessage("canvas", "update", canvas.getData());

        try {
            session.broadcastMessage(appMessage, Collections.singletonList(connectionId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
