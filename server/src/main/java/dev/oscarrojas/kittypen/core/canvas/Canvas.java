package dev.oscarrojas.kittypen.core.canvas;

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
        this.data = new byte[data.length];
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

    public void putData(CanvasFrame frame) {
        byte[] src = frame.getData();
        int startX = frame.getStartX();
        int startY = frame.getStartY();
        int width = frame.getEndX() - startX;
        int height = frame.getEndY() - startY;

        for (int i = 0; i < height; i++) {
            int rowIndex = (startY + i) * (this.width * 4) + (startX * 4);
            for (int j = 0; j < width; j++) {
                int destIndex = rowIndex + j * 4;
                int srcIndex = (i * width + j) * 4;
                data[destIndex] = src[srcIndex];
                data[destIndex + 1] = src[srcIndex + 1];
                data[destIndex + 2] = src[srcIndex + 2];
                data[destIndex + 3] = src[srcIndex + 3];
            }
        }
    }

    /**
     * Returns the entire array of image data for the canvas.
     *
     * @return full canvas image data
     */
    public byte[] getData() {
        return data;
    }

    public CanvasFrame getFrame() {
        return new CanvasFrame(
            0,
            0,
            this.width,
            this.height,
            getData()
        );
    }

    /**
     * Returns a byte array representing the underlying image data for a specified
     * portion of the canvas.
     *
     * @param x      starting x position
     * @param y      starting y position
     * @param width  rectangle width
     * @param height rectangle height
     * @return specified portion of canvas image data
     */
    byte[] getData(int x, int y, int width, int height) {
        byte[] data = new byte[width * height * 4];
        for (int i = 0; i < height; i++) {
            int start = (y + i) * (this.width * 4) + x * 4;
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
