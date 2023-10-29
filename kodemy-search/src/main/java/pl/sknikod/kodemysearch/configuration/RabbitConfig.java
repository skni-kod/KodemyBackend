package pl.sknikod.kodemysearch.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@AllArgsConstructor
@DependsOn("rabbitConfig.QueueProperties")
@Slf4j
public class RabbitConfig {
    private final ConfigurableApplicationContext applicationContext;
    private final QueueProperties queueProperties;

    @PostConstruct
    public void registerQueues() {
        queueProperties.getQueues()
                .values()
                .forEach(q -> bindQueueToExchange(registerQueue(q), registerExchange(q)));
    }

    private Queue registerQueue(QueueProperties.Queue q) {
        var queue = new Queue(q.name);
        applicationContext.getBeanFactory().registerSingleton(q.name, queue);
        log.info("Register queue [{}]", queue.getName());
        return queue;
    }

    private Exchange registerExchange(QueueProperties.Queue q) {
        var exchange = switch (q.exchange) {
            case DIRECT: yield new DirectExchange(q.name);
            case FANOUT: yield new FanoutExchange(q.name);
        };
        applicationContext.getBeanFactory().registerSingleton(q.name + ".exchange", exchange);
        return exchange;
    }

    private void bindQueueToExchange(Queue queue, Exchange exchange) {
        var binding = BindingBuilder.bind(queue).to(exchange).with("").noargs();
        applicationContext.getBeanFactory().registerSingleton(queue.getName() + ".binding", binding);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
            arg1.writeString(arg0.toString());
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException {
            return LocalDateTime.parse(arg0.getText());
        }
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
            Type exchange;

            public enum Type {
                DIRECT,
                FANOUT,
            }
        }

    }
}