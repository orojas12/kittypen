package dev.oscarrojas.kittypen.config;

import dev.oscarrojas.kittypen.ws.protocol.AppEventBinaryConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

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
            registry.addHandler(handler, "/whiteboard")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:5173");
        }
    }

    @Bean
    ServletServerContainerFactoryBean createContainer() {
        var container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(AppEventBinaryConverter.BASE_FRAME_SIZE + 64000000);
        return container;
    }

}
