package dev.oscarrojas.kittypen.room;

import dev.oscarrojas.kittypen.canvas.Canvas;

import java.util.Set;

public interface Room {

    String getId();

    Canvas getCanvas();

    Set<String> getClients();

    void addClient(String clientId);

    boolean hasClient(String clientId);

    void removeClient(String clientId);

}
