package dev.oscarrojas.kittypen.event;

public interface RoomEventStrategy {

    void handleRoomEvent(RoomEvent<?> event);

}
