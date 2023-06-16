package pl.sknikod.kodemy.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;


@Configuration
@EnableRabbit
@AllArgsConstructor
public class RabbitMQConfig {
    public static final String NOTIFICATION_USER_QUEUE_NAME = "q.notification.user";
    public static final String NOTIFICATION_ADMIN_QUEUE_NAME = "q.notification.admin";
    public static final String NOTIFICATION_EXCHANGE_NAME = "notification-exchange";
    public static final String NOTIFICATION_USER_KEY = "user";
    public static final String NOTIFICATION_ADMIN_KEY = "admin";
    private final CachingConnectionFactory cachingConnectionFactory;

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Queue notificationUserQueue() {
        return new Queue(NOTIFICATION_USER_QUEUE_NAME, true);
    }

    @Bean
    public Queue notificationAdminQueue() {
        return new Queue(NOTIFICATION_ADMIN_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    Binding notificationUserBinding(Queue notificationUserQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationUserQueue).to(exchange).with(NOTIFICATION_USER_KEY);
    }

    @Bean
    public Binding notificationAdminBinding(Queue notificationAdminQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationAdminQueue).to(exchange).with(NOTIFICATION_ADMIN_KEY);
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
}