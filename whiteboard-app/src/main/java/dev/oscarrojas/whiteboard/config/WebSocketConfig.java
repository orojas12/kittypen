package dev.oscarrojas.whiteboard.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private List<TextWebSocketHandler> webSocketHandlers;

  WebSocketConfig(List<TextWebSocketHandler> webSocketHandlers) {
    this.webSocketHandlers = webSocketHandlers;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

    for (TextWebSocketHandler handler : webSocketHandlers) {
      registry.addHandler(handler, "/");
    }
  }

}
