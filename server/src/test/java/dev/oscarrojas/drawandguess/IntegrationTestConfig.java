package dev.oscarrojas.drawandguess;

import java.util.concurrent.Executor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    Executor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
