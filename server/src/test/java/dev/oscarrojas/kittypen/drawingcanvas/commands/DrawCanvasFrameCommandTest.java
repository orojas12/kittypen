package dev.oscarrojas.kittypen.drawingcanvas.commands;

import dev.oscarrojas.kittypen.core.BasicRoom;
import dev.oscarrojas.kittypen.core.InMemoryRoomRepository;
import dev.oscarrojas.kittypen.core.Room;
import dev.oscarrojas.kittypen.core.RoomService;
import dev.oscarrojas.kittypen.core.canvas.Canvas;
import dev.oscarrojas.kittypen.core.canvas.CanvasFrame;
import dev.oscarrojas.kittypen.core.canvas.CanvasFrameBinaryConverter;
import dev.oscarrojas.kittypen.core.client.Client;
import dev.oscarrojas.kittypen.core.command.Command;
import dev.oscarrojas.kittypen.core.command.CommandRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {RoomService.class, InMemoryRoomRepository.class})
class DrawCanvasFrameCommandTest {

    @Autowired
    RoomService roomService;

    @Test
    void putsFrameData() {
        // setup
        Room room = new BasicRoom(
            "room1",
            new Canvas(4, 4),
            new HashSet<>(List.of(new Client("client1", "username"))),
            "drawing_canvas"
        );
        roomService.saveRoom(room);
        byte[] frameData = new byte[]{1, 1, 1, 1, 2, 2, 2, 2};
        CanvasFrame frame = new CanvasFrame(0, 0, 2, 1, frameData);
        CanvasFrameBinaryConverter converter = new CanvasFrameBinaryConverter();
        byte[] frameBytes = converter.toBytes(frame);
        CommandRequest<byte[]> request = new CommandRequest<>(
            Instant.now(),
            "client1",
            DrawCanvasFrameCommand.name,
            frameBytes
        );

        // execute
        Command command = new DrawCanvasFrameCommand(roomService);
        command.setBinaryRequestData(request);
        command.execute();
        command.clearRequestData();

        // test if frame was applied correctly
        room = roomService.getRoom("room1").get();
        byte[] actualData = room.getCanvas().getData(
            frame.getStartX(),
            frame.getStartY(),
            frame.getWidth(),
            frame.getHeight()
        );
        for (int i = 0; i < frameData.length; i++) {
            assertEquals(frameData[i], actualData[i]);
        }
    }

}