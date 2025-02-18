package dev.oscarrojas.kittypen.canvas;

import dev.oscarrojas.kittypen.core.canvas.CanvasFrame;
import dev.oscarrojas.kittypen.core.canvas.CanvasFrameBinaryConverter;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CanvasFrameBinaryConverterTest {

    @Test
    void fromBytes() {
        // 4 32-bit integers (4 * 4 bytes) for frame bounds and 16 bytes of frame data
        ByteBuffer buffer = ByteBuffer.allocate(4 * 4 + 16);
        // startX
        buffer.putInt(1);
        // startY
        buffer.putInt(2);
        // endX
        buffer.putInt(3);
        // endY
        buffer.putInt(4);
        // frame image data
        byte[] data = new byte[]{1, 1, 1, (byte) 255, 2, 2, 2, (byte) 255, 3, 3, 3, (byte) 255, 4, 4, 4, (byte) 255};
        for (byte b : data) {
            buffer.put(b);
        }

        CanvasFrameBinaryConverter decoder = new CanvasFrameBinaryConverter();

        CanvasFrame frame = decoder.fromBytes(buffer.array());

        assertEquals(1, frame.getStartX());
        assertEquals(2, frame.getStartY());
        assertEquals(3, frame.getEndX());
        assertEquals(4, frame.getEndY());
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], frame.getData()[i]);
        }
    }

}