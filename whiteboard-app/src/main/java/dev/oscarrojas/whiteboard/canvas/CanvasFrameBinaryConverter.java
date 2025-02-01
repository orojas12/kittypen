package dev.oscarrojas.whiteboard.canvas;

import java.nio.ByteBuffer;

public class CanvasFrameBinaryDecoder {

    CanvasFrame decode(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        CanvasFrame frame = new CanvasFrame();

        frame.setStartX(buffer.getInt());
        frame.setStartY(buffer.getInt());
        frame.setEndX(buffer.getInt());
        frame.setEndY(buffer.getInt());

        int width = frame.getEndX() - frame.getStartX();
        int height = frame.getEndY() - frame.getStartY();

        frame.setData(new byte[width * height * 4]);
        buffer.get(buffer.position(), frame.getData());

        return frame;
    }

}
