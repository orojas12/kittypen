package dev.oscarrojas.whiteboard.canvas;

import java.nio.ByteBuffer;

public class CanvasFrameBinaryConverter {

    public CanvasFrame fromBytes(byte[] payload) {
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

    public byte[] toBytes(CanvasFrame frame) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4 * 4 + frame.getData().length]);

        buffer.putInt(frame.getStartX());
        buffer.putInt(frame.getStartY());
        buffer.putInt(frame.getEndX());
        buffer.putInt(frame.getEndY());

        byte[] data = frame.getData();
        for (int i = 0; i < data.length; i++) {
            buffer.put(data[i]);
        }

        return buffer.array();
    }

}
