package dev.oscarrojas.drawandguess.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    private final List<BinaryWebSocketHandler> webSocketHandlers;

    WebSocketConfig(List<BinaryWebSocketHandler> webSocketHandlers) {
        this.webSocketHandlers = webSocketHandlers;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        for (BinaryWebSocketHandler handler : webSocketHandlers) {
            registry.addHandler(handler, "/")
                    .addInterceptors(new HttpSessionHandshakeInterceptor());
        }
    }

}
