package dev.oscarrojas.drawandguess.core.lobby.canvas;

import java.nio.ByteBuffer;

public class CanvasFrameSerializer {
    public static byte[] serialize(CanvasFrame frame) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * Integer.BYTES + frame.getData().length);

        buffer.putInt(frame.getStartX());
        buffer.putInt(frame.getStartY());
        buffer.putInt(frame.getWidth());
        buffer.putInt(frame.getHeight());

        byte[] data = frame.getData();
        buffer.put(data, 0, data.length);

        return buffer.array();
    }

    public static CanvasFrame deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        CanvasFrame frame = new CanvasFrame();

        frame.setStartX(buffer.getInt());
        frame.setStartY(buffer.getInt());
        frame.setWidth(buffer.getInt());
        frame.setHeight(buffer.getInt());

        int frameDataLength = (frame.getWidth() - frame.getStartX()) * (frame.getHeight() - frame.getStartY()) * 4;

        byte[] frameData = new byte[frameDataLength];
        buffer.get(frameData, 0, frameDataLength);

        frame.setData(frameData);

        return frame;
    }
}
