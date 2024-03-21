package com.shadow.stock_flare_middleware_service.repository.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.shadow.stock_flare_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
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
    @JsonUnwrapped
    NotificationSubscriptionCompositeKey notificationSubscriptionCompositeKey;
}
