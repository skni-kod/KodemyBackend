package pl.sknikod.kodemy.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemy.notification.rabbitmq.RabbitMessageConverter;


@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Autowired
    private RabbitMessageConverter rabbitMessageConverter;

    @Bean
    public ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        var template = new RabbitTemplate(this.connectionFactory());
        template.setMessageConverter(rabbitMessageConverter);
        return template;
    }

    @Configuration
    public static class NotificationQueue {
        public static final String EXCHANGE_NAME = "notifications-exchange";
        public static final String QUEUE_NAME = "notifications-queue";
        public static final String ROUTING_KEY = "notifications-routing-key";

        @Bean
        public DirectExchange notificationsExchange() {
            return new DirectExchange(EXCHANGE_NAME);
        }

        @Bean
        public Queue notificationsQueue() {
            return new Queue(QUEUE_NAME, true);
        }

        @Bean
        public Binding notificationBinding(){
            return BindingBuilder.bind(notificationsQueue()).to(notificationsExchange()).with(ROUTING_KEY);
        }
    }
}