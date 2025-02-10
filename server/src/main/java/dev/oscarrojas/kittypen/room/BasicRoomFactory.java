package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.EventMapper;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public class BasicRoomFactory {

    private EventStrategy strategy;
    private EventMapper mapper;

    public BasicRoomFactory() {
    }

    public void setEventStrategy(EventStrategy strategy) {
        this.strategy = strategy;
    }

    public void setEventMapper(EventMapper mapper) {
        this.mapper = mapper;
    }

    public Room createRoom(String id, Canvas canvas, Set<WebSocketSession> clients) {
        return new BasicRoom(
            id,
            canvas,
            clients,
            strategy,
            mapper
        );
    }
}
