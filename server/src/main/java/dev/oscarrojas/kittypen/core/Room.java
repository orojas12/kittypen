package dev.oscarrojas.kittypen.core;

import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.client.Client;

import java.util.Set;

public interface Room {

    String getId();

    Canvas getCanvas();

    Set<Client> getClients();

    void addClient(Client clientId);

    boolean hasClient(String clientId);

    void removeClient(String clientId);

    String getStrategy();

}
