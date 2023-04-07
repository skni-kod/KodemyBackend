package pl.sknikod.kodemy.notification.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.general.MessageConversionException;
import pl.sknikod.kodemy.notification.Notification;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RabbitMessageConverter implements MessageConverter {
    private final ObjectMapper objectMapper;

    @Override
    public @NonNull Message toMessage(@NonNull Object object, @NonNull MessageProperties messageProperties) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return new Message(json.getBytes(), messageProperties);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Failed to convert object to JSON: " + object);
        }
    }

    @Override
    public @NonNull Object fromMessage(@NonNull Message message) {
        try {
            String json = new String(message.getBody());
            return objectMapper.readValue(json, Notification.class);
        } catch (IOException e) {
            throw new MessageConversionException("Failed to convert message body");
        }
    }
}
