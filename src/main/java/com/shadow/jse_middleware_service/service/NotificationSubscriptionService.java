package com.shadow.jse_middleware_service.service;

import com.shadow.jse_middleware_service.repository.NotificationSubscriptionRepository;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSubscriptionService {

    @Autowired
    private NotificationSubscriptionRepository notificationSubscriptionRepository;


    public void subscribe(String notificationType, String symbol, String mediumId) {
        NotificationSubscription notificationSubscription = new NotificationSubscription(
                new NotificationSubscriptionCompositeKey(symbol, notificationType, mediumId)
        );

        notificationSubscriptionRepository.save(notificationSubscription);
    }

    public boolean isSubscribed(String notificationType, String symbol, String mediumId){
        return notificationSubscriptionRepository.existsById(
                new NotificationSubscriptionCompositeKey(symbol, notificationType, mediumId)
        );
    }
}
