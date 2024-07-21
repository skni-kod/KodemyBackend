package pl.sknikod.kodemynotification.util.redis;

import java.util.Collection;

public interface RedisNotificationProviderDefinition {
    void create(final RedisNotification notification, Long[] userIds);

    void update(Long[] notificationIds, RedisNotification.Status status);

    Collection<RedisNotification> findAll(Long userId);
}
