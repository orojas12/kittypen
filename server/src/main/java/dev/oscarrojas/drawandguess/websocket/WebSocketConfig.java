package dev.oscarrojas.drawandguess.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketService handler;

    WebSocketConfig(WebSocketService handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ExceptionWebSocketHandlerDecorator(handler), "/lobby/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

}
