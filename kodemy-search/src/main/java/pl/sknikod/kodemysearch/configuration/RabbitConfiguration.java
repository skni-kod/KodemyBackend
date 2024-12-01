package pl.sknikod.kodemysearch.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

@Slf4j
@Configuration
public class RabbitConfiguration {
    private static final String PARKING_LOT_BEAN_SUFFIX = "ParkingLot";
    public static final String PARKING_LOT_SUFFIX = ".parking-lot";

    public RabbitConfiguration(
            GenericApplicationContext context,
            BindingServiceProperties bindingServiceProperties
    ) {
        this.declareAdditionalQueues(context, bindingServiceProperties.getBindings());
    }

    private void declareAdditionalQueues(
            GenericApplicationContext context, Map<String, BindingProperties> bindings
    ) {
        bindings.entrySet()
                .stream()
                .filter(binding -> binding.getKey().matches("[A-Za-z]+-in-\\d+"))
                .map(Map.Entry::getValue)
                .distinct()
                .forEach(bindingValue -> {
                    final var beanName = bindingValue.getDestination() + PARKING_LOT_BEAN_SUFFIX;
                    log.debug("Register bean: {}", beanName);
                    context.registerBean(beanName, Queue.class, () -> new Queue(String.format(
                            "%s.%s%s", bindingValue.getDestination(), bindingValue.getGroup(), PARKING_LOT_SUFFIX
                    )));
                });
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
