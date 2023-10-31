package pl.sknikod.kodemynotification.infrastructure.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    public void roleChange(QueueConsumer.NotificationEvent notification) {
    }
}
