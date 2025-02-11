package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.canvas.Canvas;

import java.util.Set;

public class BasicRoom implements Room {

    private final String id;
    private final Canvas canvas;
    private final Set<String> clients;

    public BasicRoom(
        String id, Canvas canvas, Set<String> clients
    ) {
        this.id = id;
        this.canvas = canvas;
        this.clients = clients;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public Set<String> getClients() {
        return clients;
    }

    @Override
    public void addClient(String clientId) {
        clients.add(clientId);
    }

    @Override
    public boolean hasClient(String clientId) {
        return clients.contains(clientId);
    }

}
