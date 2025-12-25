package dev.oscarrojas.drawandguess.core.lobby.canvas;

public class CanvasFrame {
    private int startX;
    private int startY;
    private int width;
    private int height;
    private byte[] data;

    public CanvasFrame() {
    }

    public CanvasFrame(int startX, int startY, int width, int height, byte[] data) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
