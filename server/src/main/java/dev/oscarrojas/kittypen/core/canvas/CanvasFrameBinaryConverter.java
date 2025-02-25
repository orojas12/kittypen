package dev.oscarrojas.kittypen.core.canvas;

import java.nio.ByteBuffer;

public class CanvasFrameBinaryConverter {

    public CanvasFrame fromBytes(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        CanvasFrame frame = new CanvasFrame();

        frame.setStartX(buffer.getInt());
        frame.setStartY(buffer.getInt());
        frame.setWidth(buffer.getInt());
        frame.setHeight(buffer.getInt());

        int width = frame.getWidth() - frame.getStartX();
        int height = frame.getHeight() - frame.getStartY();

        frame.setData(new byte[width * height * 4]);
        buffer.get(buffer.position(), frame.getData());

        return frame;
    }

    public byte[] toBytes(CanvasFrame frame) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4 * 4 + frame.getData().length]);

        buffer.putInt(frame.getStartX());
        buffer.putInt(frame.getStartY());
        buffer.putInt(frame.getWidth());
        buffer.putInt(frame.getHeight());

        byte[] data = frame.getData();
        for (int i = 0; i < data.length; i++) {
            buffer.put(data[i]);
        }

        return buffer.array();
    }

}
