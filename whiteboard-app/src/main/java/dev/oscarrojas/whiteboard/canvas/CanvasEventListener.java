package dev.oscarrojas.whiteboard.canvas;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.oscarrojas.whiteboard.AppSession;
import dev.oscarrojas.whiteboard.EventListener;

@Component
public class CanvasEventListener implements EventListener {

  @Override
  public List<String> getEvents() {
    return List.of("canvas.update");
  }

  @Override
  public void handleEvent(String event, AppSession session, byte[] payload) {

    System.out.print("update canvas: ");
    for (int i = 0; i < bytes.length; i++) {
      System.out.print(bytes[i] + " ");
    }

  }

}
