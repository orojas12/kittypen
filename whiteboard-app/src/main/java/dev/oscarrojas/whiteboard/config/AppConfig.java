package dev.oscarrojas.whiteboard.config;

import dev.oscarrojas.whiteboard.messaging.AppMessageBroker;
import dev.oscarrojas.whiteboard.messaging.AppMessageConsumer;
import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import dev.oscarrojas.whiteboard.messaging.annotation.Channel;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AppConfig {

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("AppMessageBroker-");
    executor.initialize();
    return executor;
  }

  @Bean
  public AppMessageBroker messageBroker(List<AppMessageConsumer> consumers) {
    AppMessageBroker broker = new AppMessageBroker();

    for (AppMessageConsumer consumer : consumers) {
      Channel channel = consumer.getClass().getDeclaredAnnotation(Channel.class);

      if (channel == null) {
        // TODO: log non-annotated consumer
        continue;
      }

      Method[] methods = consumer.getClass().getDeclaredMethods();

      for (Method method : methods) {
        Action action = method.getAnnotation(Action.class);

        if (action == null) {
          continue;
        }
      }
    }

    return broker;
  }
}
