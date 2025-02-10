package dev.oscarrojas.kittypen.session;

import dev.oscarrojas.kittypen.messaging.AppEventListener;
import dev.oscarrojas.kittypen.messaging.JsonAppEvent;
import dev.oscarrojas.kittypen.messaging.annotation.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Map;

@Component
public class AppSessionEventListener extends AppEventListener {

    private final AppSessionService sessionService;

    public AppSessionEventListener(AppSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Event("session.getDetails")
    public void getDetails(JsonAppEvent event, WebSocketSession ws) {
        AppSession session = sessionService.getSession(ws);
        AppSessionDetails details = session.getDetails();
        JsonAppEvent putDetailsEvent = new JsonAppEvent();
        putDetailsEvent.setTimestamp(Instant.now());
        putDetailsEvent.setName("session.putDetails");
        putDetailsEvent.setPayload(Map.of("id", details.id(), "users", details.users()));
        System.out.println("Sending session details: " + putDetailsEvent.getName());
        session.sendEvent(ws.getId(), putDetailsEvent);
    }
}
