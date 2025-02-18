package dev.oscarrojas.kittypen.core.canvas;

public class CanvasFrame {
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private byte[] data;

    public CanvasFrame() {
    }

    public CanvasFrame(int startX, int startY, int endX, int endY, byte[] data) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.data = data;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
