package dev.oscarrojas.kittypen.core;

import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.client.Client;

import java.util.Set;

public class RoomState {

    private final String id;
    private final int canvasWidth;
    private final int canvasHeight;
    private final byte[] canvasData;
    private final Set<Client> clients;
    private final String commandStrategy;

    public RoomState(String id, Canvas canvas, Set<Client> clients, String commandStrategy) {
        this.id = id;
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        this.canvasData = canvas.getData();
        this.clients = clients;
        this.commandStrategy = commandStrategy;
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

    public Set<Client> getClients() {
        return clients;
    }

    public String getCommandStrategy() {
        return commandStrategy;
    }
}
