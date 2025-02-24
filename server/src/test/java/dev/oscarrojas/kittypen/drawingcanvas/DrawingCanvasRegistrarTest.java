package dev.oscarrojas.kittypen.drawingcanvas;

import dev.oscarrojas.kittypen.core.InMemoryRoomRepository;
import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.client.ClientRegistrar;
import dev.oscarrojas.kittypen.core.client.ClientRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RoomService.class, InMemoryRoomRepository.class})
class DrawingCanvasRegistrarTest {

    @Autowired
    RoomService roomService;

    @Test
    void addsNewClientToRoom() {
        ClientRegistrar registrar = new DrawingCanvasRegistrar(roomService);
        ClientRegistration registration = new ClientRegistration(
            "id",
            "username",
            "drawing_canvas_registrar"
        );
        registrar.registerClient(registration);
        Optional<Room> roomOpt = roomService.getClientRoom(registration.getId());

        assertTrue(roomOpt.isPresent());

        Room room = roomOpt.get();

        assertTrue(room.getClients().stream().map(Client::getId).collect(Collectors.toSet())
            .contains(registration.getId()));
        assertEquals(1, room.getClients().size());

        roomService.deleteRoom(room.getId());
    }

}