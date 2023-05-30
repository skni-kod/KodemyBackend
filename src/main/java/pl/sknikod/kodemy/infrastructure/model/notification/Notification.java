package pl.sknikod.kodemy.infrastructure.model.notification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.configuration.RabbitMQConfig;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String message;

    @JsonSerialize(using = RabbitMQConfig.LocalDateTimeSerializer.class)
    @JsonDeserialize(using = RabbitMQConfig.LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    private boolean read;
    private Long recipientId;

    public Notification(String title, String message, Long recipientId) {
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
        this.recipientId = recipientId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", recipientId=" + recipientId +
                '}';
    }
}