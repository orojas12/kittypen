package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.canvas.Canvas;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public class RoomState {

    private String id;
    private int canvasWidth;
    private int canvasHeight;
    private byte[] canvasData;
    private Set<WebSocketSession> clients;

    public RoomState(String id, Canvas canvas, Set<WebSocketSession> clients) {
        this.id = id;
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        this.canvasData = canvas.getData();
        this.clients = clients;
    }

    public String getId() {
        return id;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public byte[] getCanvasData() {
        return canvasData;
    }

    public Set<WebSocketSession> getClients() {
        return clients;
    }

}
