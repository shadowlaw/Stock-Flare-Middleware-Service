package com.shadow.jse_middleware_service.repository;

import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, NotificationSubscriptionCompositeKey> {
}
