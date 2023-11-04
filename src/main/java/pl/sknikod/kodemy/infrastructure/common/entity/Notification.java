package pl.sknikod.kodemy.infrastructure.common.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.configuration.RabbitConfig;

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
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String message;

    @JsonSerialize(using = RabbitConfig.LocalDateTimeSerializer.class)
    @JsonDeserialize(using = RabbitConfig.LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean read;
    @Column(nullable = false)
    private Long recipientId;

    public Notification(String title, String message, Long recipientId) {
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
        this.recipientId = recipientId;
    }
}