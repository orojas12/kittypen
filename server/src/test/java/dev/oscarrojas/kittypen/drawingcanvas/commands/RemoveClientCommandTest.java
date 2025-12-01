package dev.oscarrojas.kittypen.drawingcanvas.commands;

import dev.oscarrojas.kittypen.core.BasicRoom;
import dev.oscarrojas.kittypen.core.InMemoryRoomRepository;
import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.command.Command;
import dev.oscarrojas.kittypen.core.command.CommandRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RoomService.class, InMemoryRoomRepository.class})
class RemoveClientCommandTest {

    @Autowired
    RoomService roomService;

    @Test
    void removesClientFromRoom() {
        Room room = new BasicRoom(
            "room1",
            new Canvas(1, 1, new byte[0]),
            new HashSet<>(List.of(new Client("client1", "username"))),
            "drawing_canvas"
        );
        roomService.saveRoom(room);
        CommandRequest<Map<String, Object>> request = new CommandRequest<>(
            Instant.now(),
            "client1",
            "remove_client",
            Collections.emptyMap()
        );
        Command command = new RemoveClientCommand(roomService);
        command.setRequestData(request);
        command.execute();
        command.clearRequestData();
        Optional<Room> roomOpt = roomService.getRoom("room1");

        assertTrue(roomOpt.isPresent());

        room = roomOpt.get();

        assertEquals(0, room.getClients().size());
    }

}