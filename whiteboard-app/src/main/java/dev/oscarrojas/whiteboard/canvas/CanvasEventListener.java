package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.messaging.AppEventListener;
import dev.oscarrojas.whiteboard.messaging.BinaryAppEvent;
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
    public void putFrame(BinaryAppEvent event, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);
        Canvas canvas = session.getCanvas();
        CanvasFrame frame = converter.fromBytes(event.getPayload());
        canvas.putData(frame);
        session.broadcastEvent(event, Collections.singletonList(ws.getId()));
        sessionService.saveSession(session);
    }

    @Event("canvas.getCanvas")
    public void getCanvas(BinaryAppEvent event, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);
        Canvas canvas = session.getCanvas();
        CanvasFrame frame = canvas.getFrame();
        byte[] payload = converter.toBytes(frame);
        session.sendEvent(ws.getId(), new BinaryAppEvent("canvas.putFrame", payload));
    }
}
