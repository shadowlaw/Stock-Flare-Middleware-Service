package com.shadow.jse_middleware_service.repository.entity;

import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "notification_subscription")
public class NotificationSubscription {

    @EmbeddedId
    NotificationSubscriptionCompositeKey notificationSubscriptionCompositeKey;
}
