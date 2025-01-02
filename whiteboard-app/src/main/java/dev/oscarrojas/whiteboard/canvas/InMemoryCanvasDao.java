package dev.oscarrojas.whiteboard.canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCanvasDao implements CanvasDao {

  private Map<String, byte[]> data;

  public InMemoryCanvasDao() {
    data = new HashMap<>();
  }

  @Override
  public void save(Canvas canvas) {
    data.put(canvas.getId(), canvas.getData().getArray());
  }

  @Override
  public Optional<Canvas> get(String id) {
    byte[] arr = data.get(id);
    if (arr == null) {
      return Optional.empty();
    } else {
      Canvas canvas = new Canvas();
      canvas.putData(new UnsignedByteArray(arr));
      return Optional.of(canvas);
    }
  }

}
