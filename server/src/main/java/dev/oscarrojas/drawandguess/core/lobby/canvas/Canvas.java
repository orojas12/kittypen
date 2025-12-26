package dev.oscarrojas.drawandguess.core.lobby.canvas;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class Canvas {

    private String id = UUID.randomUUID().toString();
    private int width;
    private int height;
    private byte[] data;
    private Instant lastUpdated = Instant.ofEpochMilli(Instant.now().toEpochMilli());

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new byte[width * height * 4];
    }

    public Canvas(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public Canvas(Canvas canvas) {
        this.id = canvas.getId();
        this.width = canvas.width;
        this.height = canvas.height;
        this.data = Arrays.copyOf(canvas.getData(), canvas.getData().length);
        this.lastUpdated = canvas.getLastUpdated();
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void putFrame(CanvasFrame frame) {
        byte[] frameData = frame.getData();
        int startX = frame.getStartX();
        int startY = frame.getStartY();
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();

        for (int frameY = 0; frameY < frameHeight; frameY++) {
            for (int frameX = 0; frameX < frameWidth; frameX++) {
                int x = startX + frameX;
                int y = startY + frameY;
                int index = (y * this.width + x) * 4;
                int frameIndex = (frameY * frameWidth + frameX) * 4;
                data[index] = frameData[frameIndex];
                data[index + 1] = frameData[frameIndex + 1];
                data[index + 2] = frameData[frameIndex + 2];
                data[index + 3] = frameData[frameIndex + 3];
            }
        }
    }

    /**
     * Returns a CanvasFrame object representing the image data for a specified portion of the
     * canvas.
     *
     * @param startX starting x position
     * @param startY starting y position
     * @param width  rectangle width
     * @param height rectangle height
     * @return CanvasFrame representing image data of a portion of the canvas
     */
    public CanvasFrame getFrame(int startX, int startY, int width, int height) {
        return new CanvasFrame(
                0,
                0,
                width,
                height,
                getData(startX, startY, width, height)
        );
    }

    /**
     * Returns a byte array representing the image data for a specified portion of the canvas.
     *
     * @param startX starting x position
     * @param startY starting y position
     * @param width  rectangle width
     * @param height rectangle height
     * @return specified portion of canvas image data
     */
    public byte[] getData(int startX, int startY, int width, int height) {
        byte[] data = new byte[width * height * 4];
        for (int i = 0; i < height; i++) {
            int start = (startY + i) * (this.width * 4) + startX * 4;
            for (int j = 0; j < width * 4; j += 4) {
                int srcIndex = start + j;
                int dstIndex = i * width * 4 + j;
                data[dstIndex] = this.data[srcIndex];
                data[dstIndex + 1] = this.data[srcIndex + 1];
                data[dstIndex + 2] = this.data[srcIndex + 2];
                data[dstIndex + 3] = this.data[srcIndex + 3];
            }
        }
        return data;
    }

    /**
     * Returns the entire array of image data for the canvas.
     *
     * @return full canvas image data
     */
    public byte[] getData() {
        return data;
    }

    void reset() {
        Arrays.fill(data, (byte) 0);
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
