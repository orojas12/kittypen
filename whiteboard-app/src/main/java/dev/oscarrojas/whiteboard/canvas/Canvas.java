package dev.oscarrojas.whiteboard.canvas;

import java.util.UUID;

public class Canvas {

  private final int DEFAULT_WIDTH = 1000;
  private final int DEFAULT_HEIGHT = 1000;

  private String id = UUID.randomUUID().toString();
  private int width;
  private int height;
  private UnsignedByteArray data;

  public Canvas() {
    this.width = DEFAULT_WIDTH;
    this.height = DEFAULT_HEIGHT;
  }

  public Canvas(int width, int height) {
    this.width = width;
    this.height = height;
    this.data = new UnsignedByteArray(width * height * 4);
  }

  public Canvas(int width, int height, UnsignedByteArray data) {
    this.width = width;
    this.height = height;
    this.data = new UnsignedByteArray(data);
  }

  String getId() {
    return id;
  }

  void setId(String id) {
    this.id = id;
  }

  void putData(UnsignedByteArray array) {
    data = array;
  }

  void drawLine(int startX, int startY, int endX, int endY, Rgba rgba) {
    int dx = endX - startX;
    int dy = endY - startY;
    int step = Math.max(Math.abs(dx), Math.abs(dy));

    if (step == 0) {
      drawPixel(startX, startY, rgba);
    } else {
      float stepX = (float) dx / step;
      float stepY = (float) dy / step;
      for (int i = 0; i < step + 1; i++) {
        int x = Math.round(startX + i * stepX);
        int y = Math.round(startY + i * stepY);
        drawPixel(x, y, rgba);
      }
    }
  }

  void drawPixel(int x, int y, Rgba rgba) {
    int index = (y * width + x) * 4;
    data.set(index, rgba.getRed());
    data.set(index + 1, rgba.getBlue());
    data.set(index + 2, rgba.getGreen());
    data.set(index + 3, rgba.getAlpha());
  }

  void reset() {
    for (int i = 0; i < data.size(); i++) {
      data.set(i, 0);
    }
  }

  UnsignedByteArray getData() {
    return data;
  }

}
