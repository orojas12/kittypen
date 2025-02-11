package dev.oscarrojas.kittypen.event;

public interface ClientEventStrategy {

    RoomEvent<?> handleClientEvent(ClientEvent<?> event);

}
