package dev.oscarrojas.whiteboard.canvas;

import java.util.Optional;

public interface CanvasDao {

  void save(Canvas canvas);

  Optional<Canvas> get(String id);

}
