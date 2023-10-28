package pl.sknikod.kodemysearch.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@AllArgsConstructor
@DependsOn("rabbitConfig.QueueProperties")
public class RabbitConfig {
    private final ConfigurableApplicationContext applicationContext;
    private final QueueProperties queueProperties;


    @PostConstruct
    public void registerQueues() {
        queueProperties.getQueues()
                .stream()
                .map(Queue::new)
                .forEach(queue -> applicationContext.getBeanFactory().registerSingleton(queue.getName(), queue));
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "kodemy.rabbit")
    public static class QueueProperties {
        private List<String> queues;
    }
}