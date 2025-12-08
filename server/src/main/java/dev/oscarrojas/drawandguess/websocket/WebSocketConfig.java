package dev.oscarrojas.drawandguess.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;

import static dev.oscarrojas.drawandguess.websocket.protocol.CommandMessageMapper.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final int MAX_PAYLOAD_BYTE_LENGTH = 64000000;

    private final List<BinaryWebSocketHandler> webSocketHandlers;

    WebSocketConfig(List<BinaryWebSocketHandler> webSocketHandlers) {
        this.webSocketHandlers = webSocketHandlers;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        for (BinaryWebSocketHandler handler : webSocketHandlers) {
            registry.addHandler(handler, "/")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:5173");
        }
    }

    @Bean
    ServletServerContainerFactoryBean createContainer() {
        var container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(
            TIMESTAMP_HEADER_BYTES
                + COMMAND_HEADER_BYTES
                + MAX_COMMAND_BYTES
                + PAYLOAD_HEADER_BYTES
                + MAX_PAYLOAD_BYTE_LENGTH
        );
        return container;
    }

}
