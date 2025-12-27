package dev.oscarrojas.drawandguess.canvas;

import dev.oscarrojas.drawandguess.core.lobby.canvas.CanvasFrame;
import dev.oscarrojas.drawandguess.core.lobby.canvas.CanvasFrameSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CanvasFrameSerializerTest {

    @Test
    void serializeAndDeserialize_serializesAndDeserializesConsistently() {
        byte[] frameData = new byte[]{
                1, 1, 1, (byte) 255,
                2, 2, 2, (byte) 255,
                3, 3, 3, (byte) 255,
                4, 4, 4, (byte) 255
        };
        CanvasFrame frame = new CanvasFrame(0, 0, 2, 2, frameData);

        byte[] serialized = CanvasFrameSerializer.serialize(frame);
        CanvasFrame deserialized = CanvasFrameSerializer.deserialize(serialized);

        assertEquals(frame.getStartX(), deserialized.getStartX());
        assertEquals(frame.getStartY(), deserialized.getStartY());
        assertEquals(frame.getWidth(), deserialized.getWidth());
        assertEquals(frame.getHeight(), deserialized.getHeight());
        assertArrayEquals(frameData, deserialized.getData());
    }

}