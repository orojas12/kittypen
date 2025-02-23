package dev.oscarrojas.kittypen.drawingcanvas;

import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.client.ClientRegistrar;
import dev.oscarrojas.kittypen.core.client.ClientRegistration;
import org.springframework.stereotype.Service;

@Service
public class DrawingCanvasRegistrar implements ClientRegistrar {

    private static final String name = "drawing_canvas_registrar";
    private final RoomService roomService;

    public DrawingCanvasRegistrar(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public String getRegistrarName() {
        return name;
    }

    @Override
    public void registerClient(ClientRegistration clientRegistration) {
        Room room = roomService.findAvailableRoom(DrawingCanvasRequestStrategy.name);
        room.addClient(new Client(
            clientRegistration.getId(),
            clientRegistration.getUsername()
        ));
        roomService.saveRoom(room);
    }
}
