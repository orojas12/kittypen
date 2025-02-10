package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.ws.protocol.Event;
import org.springframework.web.socket.WebSocketSession;

public interface EventStrategy {

    void handleEvent(Event<?> event, WebSocketSession client, Room room);

}
