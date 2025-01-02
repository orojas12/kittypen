package dev.oscarrojas.whiteboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.oscarrojas.whiteboard.canvas.UnsignedByteArray;

public class UnsignedByteArrayTest {

  @Test
  void test() {
    UnsignedByteArray bytes = new UnsignedByteArray(4);

    bytes.set(0, 255);

    assertEquals(255, bytes.get(0));

    bytes.set(0, 0);

    assertEquals(0, bytes.get(0));

    bytes.set(0, -1);

    assertEquals(0, bytes.get(0));

    bytes.set(0, 256);

    assertEquals(255, bytes.get(0));

  }
}
