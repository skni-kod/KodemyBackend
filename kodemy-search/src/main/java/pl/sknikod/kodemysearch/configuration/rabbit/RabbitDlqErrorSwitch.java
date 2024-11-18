package pl.sknikod.kodemysearch.configuration.rabbit;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.configuration.RabbitConfiguration;

import java.util.Map;

@Component
@DependsOn("rabbitConfiguration")
@RequiredArgsConstructor
public class RabbitDlqErrorSwitch implements RabbitListenerConfigurer {
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
        private static final String RETRY_HEADER = "x-retries";
        private static final String MAX_LENGTH_HEADER = "x-max-length";
        private final RabbitTemplate rabbitTemplate;

        @Override
        public void onMessage(Message message) {
            final var headers = message.getMessageProperties().getHeaders();
            final var routingKey = message.getMessageProperties().getReceivedRoutingKey();
            final var retry = (int) headers.getOrDefault(RETRY_HEADER, 0);
            final var maxLength = (int) headers.getOrDefault(MAX_LENGTH_HEADER, 3);
            if (retry >= maxLength) {
                redirect(routingKey, DlqMessageListener.RedirectTarget.PARKING_LOT, message);
                return;
            }
            headers.put(RETRY_HEADER, retry + 1);
            redirect(routingKey, DlqMessageListener.RedirectTarget.ORIGINAL, message);
        }

        private void redirect(String routingKey, DlqMessageListener.RedirectTarget redirectTarget, Message message) {
            String newRoutingKey = routingKey.replace(DLQ_SUFFIX, "") + redirectTarget.suffix;
            rabbitTemplate.send("", newRoutingKey, message);
        }

        @AllArgsConstructor
        private enum RedirectTarget {
            ORIGINAL(""),
            PARKING_LOT(RabbitConfiguration.PARKING_LOT_SUFFIX);
            private final String suffix;
        }
    }
}
