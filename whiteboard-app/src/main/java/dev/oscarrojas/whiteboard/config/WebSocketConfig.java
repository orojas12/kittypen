package dev.oscarrojas.whiteboard.config;

import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

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

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // max payload size per message = 64,000,000 bytes
        container.setMaxBinaryMessageBufferSize(AppMessageBinaryEncoder.BASE_FRAME_SIZE + 64000000);
        return container;
    }

}
