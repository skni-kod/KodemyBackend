package pl.sknikod.kodemybackend.configuration;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;

import java.util.Map;

@Slf4j
@Configuration
public class RabbitConfig {
    private static final String PARKING_LOT_BEAN_SUFFIX = "ParkingLot";
    public static final String PARKING_LOT_SUFFIX = ".parking-lot";

    public RabbitConfig(
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

    @Component
    @DependsOn("rabbitConfig")
    @RequiredArgsConstructor
    public static class DlqRabbitSwitch implements RabbitListenerConfigurer {
        public static final String DLQ_SUFFIX = ".dlq";
        private final RabbitTemplate rabbitTemplate;
        private final BindingServiceProperties bindingServiceProperties;

        @Override
        public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
            registrar.registerEndpoint(new DlqRabbitListenerEndpoint());
        }

        private class DlqRabbitListenerEndpoint extends AbstractRabbitListenerEndpoint {
            @Override
            @NonNull
            protected MessageListener createMessageListener(@NonNull MessageListenerContainer container) {
                final var queueNames = bindingServiceProperties
                        .getBindings().entrySet().stream()
                        .filter(binding -> binding.getKey().matches("[A-Za-z]+-in-\\d+"))
                        .map(Map.Entry::getValue)
                        .map(binding -> String.format("%s.%s%s", binding.getDestination(), binding.getGroup(), DLQ_SUFFIX))
                        .toArray(String[]::new);
                container.setQueueNames(queueNames);
                return new DlqMessageListener(rabbitTemplate);
            }

            @Override
            @NonNull
            public String getId() {
                return getClass().getName();
            }
        }

        @RequiredArgsConstructor
        private static class DlqMessageListener implements MessageListener {
            private static final String RETRY_HEADER = "x-dead-letter-retry";
            private static final String MAX_LENGTH_HEADER = "x-max-length";
            private final RabbitTemplate rabbitTemplate;

            @Override
            public void onMessage(Message message) {
                final var headers = message.getMessageProperties().getHeaders();
                final var routingKey = message.getMessageProperties().getReceivedRoutingKey();
                final var retry = (int) headers.getOrDefault(RETRY_HEADER, 0);
                final var maxLength = (int) headers.getOrDefault(MAX_LENGTH_HEADER, 3);
                if (retry >= maxLength) {
                    redirect(routingKey, RedirectTarget.PARKING_LOT, message);
                    return;
                }
                headers.put(RETRY_HEADER, retry + 1);
                redirect(routingKey, RedirectTarget.ORIGINAL, message);
            }

            private void redirect(String routingKey, RedirectTarget redirectTarget, Message message) {
                String newRoutingKey = routingKey.replace(DLQ_SUFFIX, "") + redirectTarget.suffix;
                rabbitTemplate.send("", newRoutingKey, message);
            }

            @AllArgsConstructor
            private enum RedirectTarget {
                ORIGINAL(""),
                PARKING_LOT(PARKING_LOT_SUFFIX);
                private final String suffix;
            }
        }
    }
}
