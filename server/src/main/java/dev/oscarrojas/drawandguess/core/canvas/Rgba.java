package dev.oscarrojas.drawandguess.core.canvas;

public class Rgba {

    private byte red;
    private byte green;
    private byte blue;
    private byte alpha;

    Rgba(int red, int green, int blue, int alpha) {
        this.red = (byte) red;
        this.green = (byte) green;
        this.blue = (byte) blue;
        this.alpha = (byte) alpha;
    }

    public int getRed() {
        return red < 0 ? red + 256 : red;
    }

    public void setRed(int red) {
        this.red = (byte) red;
    }

    public int getGreen() {
        return green < 0 ? green + 256 : green;
    }

    public void setGreen(int green) {
        this.green = (byte) green;
    }

    public int getBlue() {
        return blue < 0 ? blue + 256 : blue;
    }

    public void setBlue(int blue) {
        this.blue = (byte) blue;
    }

    public int getAlpha() {
        return alpha < 0 ? alpha + 256 : alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = (byte) alpha;
    }

}
