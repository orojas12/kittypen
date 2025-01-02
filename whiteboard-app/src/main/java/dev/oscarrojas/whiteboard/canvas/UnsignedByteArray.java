package dev.oscarrojas.whiteboard.canvas;

public class UnsignedByteArray {

  private byte[] array;

  public UnsignedByteArray(int size) {
    this.array = new byte[size];
  }

  public UnsignedByteArray(byte[] array) {
    this.array = new byte[array.length];
    for (int i = 0; i < this.array.length; i++) {
      this.array[i] = array[i];
    }
  }

  public UnsignedByteArray(UnsignedByteArray byteArray) {
    byte[] array = byteArray.getArray();
    this.array = new byte[array.length];
    for (int i = 0; i < this.array.length; i++) {
      this.array[i] = array[i];
    }
  }

  public int get(int index) {
    return (int) array[index] & 0xFF;
  }

  public void set(int index, int value) {
    if (value > 255) {
      array[index] = (byte) 255;
    } else if (value < 0) {
      array[index] = 0;
    } else {
      array[index] = (byte) (value);
    }
  }

  public byte[] getArray() {
    return array;
  }

  public int size() {
    return array.length;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append('[');
    for (int i = 0; i < array.length; i++) {
      str.append(array[i]);
      if (!(i == array.length - 1)) {
        str.append(", ");
      }
    }
    str.append(']');
    return str.toString();
  }
}
