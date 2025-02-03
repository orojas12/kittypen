package dev.oscarrojas.whiteboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.List;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private List<BinaryWebSocketHandler> webSocketHandlers;

    WebSocketConfig(List<BinaryWebSocketHandler> webSocketHandlers) {
        this.webSocketHandlers = webSocketHandlers;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        for (BinaryWebSocketHandler handler : webSocketHandlers) {
            registry.addHandler(handler, "/").setAllowedOrigins("http://localhost:5173");
        }
    }


}
