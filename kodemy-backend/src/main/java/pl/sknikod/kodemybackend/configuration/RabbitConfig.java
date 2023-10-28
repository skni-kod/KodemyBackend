package pl.sknikod.kodemybackend.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@AllArgsConstructor
@DependsOn("rabbitConfig.QueueProperties")
public class RabbitConfig {
    private final ConfigurableApplicationContext applicationContext;
    private final QueueProperties queueProperties;


    @PostConstruct
    public void registerQueues() {
        queueProperties.getQueues()
                .values()
                .stream()
                .map(QueueProperties.Queue::getName)
                .map(Queue::new)
                .forEach(queue -> applicationContext.getBeanFactory().registerSingleton(queue.getName(), queue));
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "kodemy.rabbit")
    public static class QueueProperties {
        private Map<String, Queue> queues;

        public Queue get(String key) {
            return queues.get(key);
        }

        @Data
        public static class Queue {
            String name;
        }
    }
}