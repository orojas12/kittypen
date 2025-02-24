package dev.oscarrojas.kittypen.core;

import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.client.Client;

import java.util.Set;

public class BasicRoom implements Room {

    private final String id;
    private final Canvas canvas;
    private final Set<Client> clients;
    private final String strategy;

    public BasicRoom(
        String id, Canvas canvas, Set<Client> clients, String strategy
    ) {
        this.id = id;
        this.canvas = canvas;
        this.clients = clients;
        this.strategy = strategy;
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
    public Set<Client> getClients() {
        return clients;
    }

    @Override
    public void addClient(Client client) {
        clients.add(client);
    }

    @Override
    public boolean hasClient(String clientId) {
        for (Client client : clients) {
            if (client.getId().equals(clientId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeClient(String clientId) {
        clients.removeIf(client -> client.getId().equals(clientId));
    }

    @Override
    public String getCommandStrategy() {
        return strategy;
    }
}
