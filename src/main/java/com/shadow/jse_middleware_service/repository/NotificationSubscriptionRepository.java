package com.shadow.jse_middleware_service.repository;

import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, NotificationSubscriptionCompositeKey> {

    Page<NotificationSubscription> findByNotificationSubscriptionCompositeKeyMediumId(String mediumId, Pageable pageRequest);
    Page<NotificationSubscription> findByNotificationSubscriptionCompositeKeyMediumIdAndNotificationSubscriptionCompositeKeyNotificationTypeIn(String mediumId, List<String> subscriptionType,Pageable pageRequest);
}
