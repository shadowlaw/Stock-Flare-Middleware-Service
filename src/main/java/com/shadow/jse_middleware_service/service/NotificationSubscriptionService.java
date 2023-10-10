package com.shadow.jse_middleware_service.service;

import com.shadow.jse_middleware_service.repository.NotificationSubscriptionRepository;
import com.shadow.jse_middleware_service.repository.entity.NotificationSubscription;
import com.shadow.jse_middleware_service.repository.entity.key.NotificationSubscriptionCompositeKey;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public void deleteSubscription(NotificationSubscription subscription) {
        notificationSubscriptionRepository.delete(subscription);
    }

    public Optional<NotificationSubscription> getSubscription(String notificationType, String symbol, String mediumId) {
        if (!isSubscribed(notificationType, symbol, mediumId)) {
            return Optional.empty();
        }
        return Optional.of(notificationSubscriptionRepository.getById(new NotificationSubscriptionCompositeKey(symbol, notificationType, mediumId)));
    }

    public Page<NotificationSubscription> getSubscriptionsByPage(String mediumId, PageRequest pageRequest) {
        return notificationSubscriptionRepository.findByNotificationSubscriptionCompositeKeyMediumId(mediumId, pageRequest);
    }
}
