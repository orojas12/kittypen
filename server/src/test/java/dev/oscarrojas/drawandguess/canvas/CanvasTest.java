package dev.oscarrojas.drawandguess.canvas;

import dev.oscarrojas.drawandguess.core.lobby.canvas.Canvas;
import dev.oscarrojas.drawandguess.core.lobby.canvas.CanvasFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CanvasTest {

    @Test
    void putFrame_putsDataCorrectly() {
        byte[] frameData = new byte[]{
                1, 1, 1, (byte) 255,
                2, 2, 2, (byte) 255,
                3, 3, 3, (byte) 255,
                4, 4, 4, (byte) 255
        };
        CanvasFrame frame = new CanvasFrame(1, 1, 2, 2, frameData);

        // 4x4 grid (4 bytes each pixel for rgba)
        byte[] data = new byte[]{
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
        };
        Canvas canvas = new Canvas(4, 4, data);

        canvas.putFrame(frame);

        byte[] expected = new byte[]{
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1, 1, 1, (byte) 255,
                2, 2, 2, (byte) 255,
                0, 0, 0, 0,
                0, 0, 0, 0,
                3, 3, 3, (byte) 255,
                4, 4, 4, (byte) 255,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
        };

        assertArrayEquals(expected, canvas.getData());
    }
}
