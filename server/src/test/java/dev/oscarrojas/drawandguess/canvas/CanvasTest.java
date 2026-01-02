package dev.oscarrojas.drawandguess.canvas;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import dev.oscarrojas.drawandguess.core.lobby.canvas.Canvas;
import dev.oscarrojas.drawandguess.core.lobby.canvas.CanvasFrame;
import org.junit.jupiter.api.Test;

public class CanvasTest {

    @Test
    void putFrame_putsFrameData() {
        // rectangle of rgba data (width = 2; height = 2)
        byte[] src = new byte[] {
            1, 1, 1, (byte) 255,
            2, 2, 2, (byte) 255,
            3, 3, 3, (byte) 255,
            4, 4, 4, (byte) 255
        };

        CanvasFrame frame = new CanvasFrame(1, 1, 2, 2, src);

        // 4x4 grid
        byte[] dst = new byte[] {
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

        byte[] expected = new byte[] {
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

        Canvas canvas = new Canvas(4, 4, dst);
        canvas.putFrame(frame);

        assertArrayEquals(expected, dst);
    }

    @Test
    void getFrame_getsFrameData() {

        // 4x4 grid
        byte[] src = new byte[] {
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

        Canvas canvas = new Canvas(4, 4, src);

        // 2x2 square
        byte[] expected = new byte[] {
            1, 1, 1, (byte) 255,
            2, 2, 2, (byte) 255,
            3, 3, 3, (byte) 255,
            4, 4, 4, (byte) 255
        };

        byte[] frame = canvas.getFrame(1, 1, 2, 2).getData();

        assertArrayEquals(expected, frame);
    }
}
