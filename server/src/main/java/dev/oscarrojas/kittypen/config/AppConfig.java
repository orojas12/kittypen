package dev.oscarrojas.kittypen.config;

import dev.oscarrojas.kittypen.messaging.AppEventEmitter;
import dev.oscarrojas.kittypen.messaging.AppEventListener;
import dev.oscarrojas.kittypen.ws.protocol.WebSocketEventMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@Configuration
@EnableAsync
public class AppConfig {

    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public AppEventEmitter eventEmitter(List<AppEventListener> listeners) {
        AppEventEmitter emitter = new AppEventEmitter();

        for (AppEventListener listener : listeners) {
            Iterable<AppEventListener.ListenerMethod> methods = listener.getListenerMethods();
            for (var method : methods) {
                emitter.addEventListener(method.event, listener);
            }
        }

        return emitter;
    }

    @Bean
    public BasicRoomFactory basicRoomFactory(WebSocketEventMapper mapper) {
        BasicRoomFactory factory = new BasicRoomFactory();
        factory.setEventMapper(mapper);
        factory.setEventStrategy(new BasicRoomStrategy());
        return factory;
    }
}
