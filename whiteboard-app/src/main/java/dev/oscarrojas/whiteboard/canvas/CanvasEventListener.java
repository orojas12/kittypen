package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.messaging.AppEvent;
import dev.oscarrojas.whiteboard.messaging.AppEventListener;
import dev.oscarrojas.whiteboard.messaging.annotation.Event;
import dev.oscarrojas.whiteboard.session.AppSession;
import dev.oscarrojas.whiteboard.session.AppSessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;

@Component
public class CanvasEventListener extends AppEventListener {

    private final CanvasFrameBinaryConverter converter = new CanvasFrameBinaryConverter();
    private final AppSessionService sessionService;

    public CanvasEventListener(AppSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Event("canvas.putFrame")
    public void putFrame(AppEvent event, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);
        Canvas canvas = session.getCanvas();
        CanvasFrame frame = converter.fromBytes(event.getPayload());
        canvas.putData(frame);
        session.broadcastEvent(event, Collections.singletonList(ws.getId()));
        sessionService.saveSession(session);
    }

    @Event("canvas.getFrame")
    public void getFrame(AppEvent event, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);

    }
}
