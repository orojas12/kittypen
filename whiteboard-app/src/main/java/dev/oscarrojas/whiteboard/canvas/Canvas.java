package dev.oscarrojas.whiteboard.canvas;

import dev.oscarrojas.whiteboard.exception.InvalidInputException;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class Canvas {

    private final int DEFAULT_WIDTH = 1;
    private final int DEFAULT_HEIGHT = 1;

    private String id = UUID.randomUUID().toString();
    private int width;
    private int height;
    private byte[] data;
    private Instant lastUpdated;

    public Canvas() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.data = new byte[width * height * 4];
        this.lastUpdated = Instant.now();
    }

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new byte[width * height * 4];
        this.lastUpdated = Instant.now();
    }

    public Canvas(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = new byte[data.length];
        this.lastUpdated = Instant.now();
    }

    public Canvas(Canvas canvas) {
        this.id = canvas.getId();
        this.width = canvas.width;
        this.height = canvas.height;
        this.data = Arrays.copyOf(canvas.getData(), canvas.getData().length);
        this.lastUpdated = Instant.ofEpochMilli(Instant.now().toEpochMilli());
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    void putData(byte[] array) throws InvalidInputException {
        if (array.length != width * height * 4) {
            throw new InvalidInputException("Input array does not match required size");
        }
        data = array;
    }

    void reset() {
        Arrays.fill(data, (byte) 0);
    }

    public byte[] getData() {
        return data;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
