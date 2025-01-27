package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.exception.InvalidInputException;
import dev.oscarrojas.whiteboard.messaging.AppMessage;
import dev.oscarrojas.whiteboard.messaging.AppMessageConsumer;
import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;

@Component
@Channel("canvas")
public class CanvasMessageConsumer extends AppMessageConsumer {

    private final AppSessionService sessionService;

    public CanvasMessageConsumer(AppSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Action("update")
    public void update(AppMessage message, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);
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
            // TODO: figure out a good way to send error message
            throw new RuntimeException(e);
        }

        canvas.setLastUpdated(message.getTimestamp());
        sessionService.saveSession(session);
        AppMessage appMessage = new AppMessage("canvas", "update", canvas.getData());
        session.broadcastMessage(appMessage, Collections.singletonList(ws.getId()));
    }
}
