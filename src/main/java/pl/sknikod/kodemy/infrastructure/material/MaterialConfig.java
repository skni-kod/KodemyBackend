package pl.sknikod.kodemy.infrastructure.material;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemy.configuration.RabbitConfig;

@Configuration
public class MaterialConfig {
    public static final String NAME_CREATED = "material.created";
    public static final String NAME_UPDATED = "material.updated";

    @Bean
    public Queue createdQueue() {
        return new Queue(NAME_CREATED, true);
    }

    @Bean
    public FanoutExchange createdExchange(Queue createdQueue) {
        return new FanoutExchange(createdQueue.getName() + RabbitConfig.EXCHANGE_SUFFIX);
    }

    @Bean
    Binding createdBinding(Queue createdQueue, FanoutExchange createdExchange) {
        return BindingBuilder.bind(createdQueue).to(createdExchange);
    }

    @Bean
    public Queue updatedQueue() {
        return new Queue(NAME_UPDATED, true);
    }

    @Bean
    public DirectExchange updatedExchange(Queue updatedQueue) {
        return new DirectExchange(updatedQueue.getName() + RabbitConfig.EXCHANGE_SUFFIX);
    }

    @Bean
    Binding updatedBinding(Queue updatedQueue, FanoutExchange updatedExchange) {
        return BindingBuilder.bind(updatedQueue).to(updatedExchange);
    }
}
